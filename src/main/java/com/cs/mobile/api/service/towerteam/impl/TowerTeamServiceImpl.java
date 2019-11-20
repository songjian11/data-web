package com.cs.mobile.api.service.towerteam.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cs.mobile.api.dao.towerteam.TowerTeamDao;
import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.towerteam.TowerTeamConfig;
import com.cs.mobile.api.model.towerteam.TowerTeamResult;
import com.cs.mobile.api.model.towerteam.response.TowerTeamAreaResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamResultResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamStoreResp;
import com.cs.mobile.api.service.common.RedisService;
import com.cs.mobile.api.service.towerteam.TowerTeamService;
import com.cs.mobile.common.constant.RedisKeyConstants;
import com.cs.mobile.common.exception.api.ExceptionUtils;
import com.cs.mobile.common.utils.DateUtil;

/**
 * 门店支援服务
 * 
 * @author wells
 * @date 2019年1月17日
 */
@Service
public class TowerTeamServiceImpl implements TowerTeamService {
	@Autowired
	TowerTeamDao towerTeamDao;
	@Autowired
	RedisService redisService;

	/**
	 * 获取所有区域
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAllProvince() throws Exception {
		return towerTeamDao.getAllProvince();
	}

	/**
	 * 根据省份获取所有大区
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAreaGroupByP(String provinceId) throws Exception {
		return towerTeamDao.getAreaGroupByP(provinceId);
	}

	/**
	 * 根据大区获取所有区域
	 * 
	 * @param areaGroupId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAreaByG(String areaGroupId) throws Exception {
		return towerTeamDao.getAreaByG(areaGroupId);
	}

	/**
	 * 根据区域信息分页获取报名门店列表
	 * 
	 * @param provinceId
	 * @param areaGroupId
	 * @param areaId
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public PageResult<TowerTeamStoreResp> getConfigByPga(String provinceId, String areaGroupId, String areaId, int page,
			int pageSize) throws Exception {
		return towerTeamDao.getConfigByPga(provinceId, areaGroupId, areaId, page, pageSize);
	}

	/**
	 * 提交报名
	 * 
	 * @param towerTeamResult
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	@Transactional
	public void submit(TowerTeamResult towerTeamResult) throws Exception {
		// 防止超过报名数量使用redis分布式锁
		String requestId = towerTeamResult.getStoreId() + "_" + towerTeamResult.getPositionId();// 请求线程ID
		String lockKey = String.format(RedisKeyConstants.TT_CONSUME_LOCK, requestId);// 报名锁KEY
		if (!redisService.tryGetDistributedLock(lockKey, requestId, 1000)) {
			ExceptionUtils.wapperBussinessException("报名太火爆了，换个姿势吧");
		}
		TowerTeamResultResp dbTowerTeamResult = towerTeamDao.getPersonResult(towerTeamResult.getPersonId());
		if (dbTowerTeamResult != null) {
			ExceptionUtils.wapperBussinessException("你已报名成功，如需更换请先取消");
		}
		TowerTeamConfig config = towerTeamDao.getOneConfigBySp(towerTeamResult.getStoreId(),
				towerTeamResult.getPositionId());
		if (config.getAvailable().intValue() == 0) {
			ExceptionUtils.wapperBussinessException("你所提交门店的该岗位人数已满");
		}
		Date now = DateUtil.getNowDate();
		towerTeamResult.setStatus(1);
		towerTeamResult.setCreateTime(now);
		towerTeamResult.setUpdateTime(now);
		int saveRet = towerTeamDao.saveResult(towerTeamResult);
		// 消费报名数量
		int updateRet = towerTeamDao.updateAvailable(-1, towerTeamResult.getStoreId(), towerTeamResult.getPositionId());
		// 释放redis分布式锁
		if (saveRet == 0 || updateRet == 0) {
			ExceptionUtils.wapperBussinessException("报名出现异常");
		}
		redisService.releaseDistributedLock(lockKey, requestId);
	}

	/**
	 * 取消报名
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	@Transactional
	public void cancel(String storeId, String positionId, String personId) throws Exception {
		TowerTeamResultResp dbTowerTeamResult = towerTeamDao.getPersonResult(personId);
		if (dbTowerTeamResult == null) {
			ExceptionUtils.wapperBussinessException("你没有成功报名，不需要取消");
		}
		if (!dbTowerTeamResult.getStoreId().equals(storeId) && !dbTowerTeamResult.getPositionId().equals(positionId)) {
			ExceptionUtils.wapperBussinessException("你取消的信息和你成功报名的信息不一致");
		}
		int cancelRet = towerTeamDao.cancel(personId);
		// 释放报名数量
		int updateRet = towerTeamDao.updateAvailable(1, storeId, positionId);
		if (cancelRet == 0 || updateRet == 0) {
			ExceptionUtils.wapperBussinessException("取消报名出现异常");
		}
	}

	/**
	 * 获取个人报名成功的信息
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public TowerTeamResultResp getPersonResult(String personId) throws Exception {
		return towerTeamDao.getPersonResult(personId);
	}
}

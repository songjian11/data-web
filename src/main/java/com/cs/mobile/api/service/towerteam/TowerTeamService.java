package com.cs.mobile.api.service.towerteam;

import java.util.List;

import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.towerteam.TowerTeamResult;
import com.cs.mobile.api.model.towerteam.response.TowerTeamAreaResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamResultResp;
import com.cs.mobile.api.model.towerteam.response.TowerTeamStoreResp;

/**
 * 门店支援服务
 * 
 * @author wells
 * @date 2019年1月17日
 */
public interface TowerTeamService {
	/**
	 * 获取所有区域
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAllProvince() throws Exception;

	/**
	 * 根据省份获取所有大区
	 * 
	 * @param provinceId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAreaGroupByP(String provinceId) throws Exception;

	/**
	 * 根据大区获取所有区域
	 * 
	 * @param areaGroupId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public List<TowerTeamAreaResp> getAreaByG(String areaGroupId) throws Exception;

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
			int pageSize) throws Exception;

	/**
	 * 提交报名
	 * 
	 * @param towerTeamResult
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public void submit(TowerTeamResult towerTeamResult) throws Exception;

	/**
	 * 取消报名
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public void cancel(String storeId, String positionId, String personId) throws Exception;

	/**
	 * 获取个人报名成功的信息
	 * 
	 * @param personId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年1月17日
	 */
	public TowerTeamResultResp getPersonResult(String personId) throws Exception;
}

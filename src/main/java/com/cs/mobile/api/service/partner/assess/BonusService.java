package com.cs.mobile.api.service.partner.assess;

import java.util.List;

import com.cs.mobile.api.model.common.PageResult;
import com.cs.mobile.api.model.partner.assess.response.BonusAssessReportResp;
import com.cs.mobile.api.model.partner.assess.response.BonusHistoryResp;
import com.cs.mobile.api.model.partner.assess.response.ComPersonBonusResp;
import com.cs.mobile.api.model.partner.assess.response.PersonBonusItemResp;
import com.cs.mobile.api.model.partner.assess.response.StoreAuditResp;
import com.cs.mobile.api.model.partner.progress.ProgressReportResult;

public interface BonusService {
	/**
	 * 根据小店获取人员奖金情况（不包括大店店长）
	 * 
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public ComPersonBonusResp getComPersonBonus(String storeId, String comId) throws Exception;

	/**
	 * 调整人员奖金
	 * 
	 * @param personBonusList
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public void modifyComPersonBonus(List<PersonBonusItemResp> personBonusList) throws Exception;

	/**
	 * 分页分页查询奖金历史列表
	 * 
	 * @param page
	 * @param pageSize
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public PageResult<BonusHistoryResp> getBonusHistory(int page, int pageSize, String storeId, String comId)
			throws Exception;

	/**
	 * 审核奖金
	 * 
	 * @param ym
	 * @param storeId
	 * @param comId
	 * @param status
	 * @param reason
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public void auditBonus(String ym, String storeId, String comId, int status, String reason) throws Exception;

	/**
	 * 获取上月门店审核列表
	 * 
	 * @param storeId
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月2日
	 */
	public StoreAuditResp getStoreAuditList(String storeId) throws Exception;

	/**
	 * 查询当前正在执行的任务数量
	 * 
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int getTaskCount() throws Exception;

	/**
	 * 更新任务
	 * 
	 * @param batchNo
	 * @param status
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int updateTask(int status) throws Exception;

	/**
	 * 插入新任务
	 * 
	 * @param batchNo
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public int addTask(String batchNo) throws Exception;

	/**
	 * 产生奖金数据
	 * 
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月3日
	 */
	public void createData() throws Exception;

	/**
	 * 获取小店奖金数据
	 * 
	 * @param comId
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getComBonusByComId(String comId, String storeId, String ym) throws Exception;

	/**
	 * 获取大店奖金数据
	 * 
	 * @param storeId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getComBonusByStoreId(String storeId, String ym) throws Exception;

	/**
	 * 获取区域奖金数据
	 * 
	 * @param areaId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getComBonusByAreaId(String areaId, String ym) throws Exception;

	/**
	 * 获取省份奖金数据
	 * 
	 * @param provinceId
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getComBonusByProvinceId(String provinceId, String ym) throws Exception;

	/**
	 * 获取所有奖金数据
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月29日
	 */
	public ProgressReportResult getAllComBonus(String ym) throws Exception;

	/**
	 * 获取指定月份奖金考核报表
	 *
	 * @param ym
	 * @param provinceId
	 * @param areaId
	 * @param storeId
	 * @return
	 * @throws Exception
	 *
	 * @author wells.wong
	 * @date 2019年7月9日
	 *
	 */
	public List<BonusAssessReportResp> getBonusAssessReportByYm(String ym, String provinceId, String areaId,
			String storeId) throws Exception;
}

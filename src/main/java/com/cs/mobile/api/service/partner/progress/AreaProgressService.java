package com.cs.mobile.api.service.partner.progress;

import java.util.List;

import com.cs.mobile.api.model.partner.battle.Accounting;
import com.cs.mobile.api.model.partner.progress.AccountingItemResult;
import com.cs.mobile.api.model.partner.progress.ProgressReportResult;
import com.cs.mobile.api.model.partner.progress.ShareDetail;

/**
 * 省份下所有区域首页进度报表服务
 * 
 * @author wells
 * @time 2018年12月13日
 */
public interface AreaProgressService {
	/**
	 * 获取进度报表数据
	 * 
	 * @author wells
	 * @param groupId
	 * @param storeId
	 * @param comId
	 * @param beginYm
	 * @param endYm
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public ProgressReportResult getProgressReport(String provinceId, String beginYmd, String endYmd) throws Exception;

	/**
	 * 获取进度报表详情（气泡图）
	 * 
	 * @author wells
	 * @param groupId
	 * @param storeId
	 * @param comId
	 * @param beginYmd
	 * @param endYmd
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public ShareDetail getProgressReportDetail(String provinceId, String beginYmd, String endYmd) throws Exception;

	/**
	 * 获取核算表
	 * 
	 * @author wells
	 * @param storeId
	 * @param comId
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public List<Accounting> getAccountingList(String provinceId) throws Exception;

	/**
	 * 获取核算表明细
	 * 
	 * @author wells
	 * @param groupId
	 * @param storeId
	 * @param comId
	 * @param beginYmd
	 * @param endYmd
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public AccountingItemResult getAccountingItem(String provinceId, String beginYmd, String endYmd) throws Exception;
}

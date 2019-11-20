package com.cs.mobile.api.service.partner.progress;

import java.util.List;

import com.cs.mobile.api.model.partner.battle.Accounting;
import com.cs.mobile.api.model.partner.progress.AccountingItemResult;
import com.cs.mobile.api.model.partner.progress.ProgressReportResult;
import com.cs.mobile.api.model.partner.progress.ShareDetail;

/**
 * 全司下所有省份首页进度报表服务
 * 
 * @author wells
 * @time 2018年12月13日
 */
public interface ProvinceProgressService {
	/**
	 * 获取进度报表数据
	 * 
	 * @author wells
	 * @param beginYm
	 * @param endYm
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public ProgressReportResult getProgressReport(String beginYmd, String endYmd) throws Exception;

	/**
	 * 获取进度报表详情（气泡图）
	 * 
	 * @author wells
	 * @param beginYmd
	 * @param endYmd
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public ShareDetail getProgressReportDetail(String beginYmd, String endYmd) throws Exception;

	/**
	 * 获取核算表
	 * 
	 * @author wells
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public List<Accounting> getAccountingList() throws Exception;

	/**
	 * 获取核算表明细
	 * 
	 * @author wells
	 * @param beginYmd
	 * @param endYmd
	 * @return
	 * @throws Exception
	 * @time 2018年12月19日
	 */
	public AccountingItemResult getAccountingItem(String beginYmd, String endYmd) throws Exception;
}

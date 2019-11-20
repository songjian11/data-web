package com.cs.mobile.api.service.partner.battle;

import com.cs.mobile.api.model.partner.battle.BattleReportResult;

public interface GrossMarginBattleService {
	/**
	 * 门店下所有小店当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComTodayGMReport(String storeId, Integer isCompare) throws Exception;

	/**
	 * 门店下所有小店当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComCurMonthGMReport(String storeId, Integer isCompare) throws Exception;

	/**
	 * 小店下所有大类当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptTodayGMReport(String storeId, String comId, Integer isCompare) throws Exception;

	/**
	 * 小店下所有大类当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptCurMonthGMReport(String storeId, String comId, Integer isCompare) throws Exception;

	/**
	 * 区域下所有门店当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreTodayGMReport(String areaId) throws Exception;

	/**
	 * 区域下所有门店当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreCurMonthGMReport(String areaId) throws Exception;

	/**
	 * 省份下所有区域当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaTodayGMReport(String provinceId) throws Exception;

	/**
	 * 省份下所有区域当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaCurMonthGMReport(String provinceId) throws Exception;

	/**
	 * 全司下所有省份当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceTodayGMReport() throws Exception;

	/**
	 * 全司下所有省份当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceCurMonthGMReport() throws Exception;
}

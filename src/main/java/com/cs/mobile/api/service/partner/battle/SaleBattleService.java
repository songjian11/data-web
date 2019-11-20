package com.cs.mobile.api.service.partner.battle;

import com.cs.mobile.api.model.partner.battle.BattleReportResult;

public interface SaleBattleService {
	/**
	 * 店下所有小店当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComTodaySaleReport(String storeId, Integer isCompare) throws Exception;

	/**
	 * 门店下所有小店当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComCurMonthSaleReport(String storeId, Integer isCompare) throws Exception;

	/**
	 * 小店下所有大类当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptTodaySaleReport(String storeId, String comId, Integer isCompare) throws Exception;

	/**
	 * 小店下所有大类当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptCurMonthSaleReport(String storeId, String comId, Integer isCompare)
			throws Exception;

	/**
	 * 区域下所有门店当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreTodaySaleReport(String areaId) throws Exception;

	/**
	 * 区域下所有门店当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreCurMonthSaleReport(String areaId) throws Exception;

	/**
	 * 省份下所有区域当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaTodaySaleReport(String provinceId) throws Exception;

	/**
	 * 省份下所有区域当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaCurMonthSaleReport(String provinceId) throws Exception;

	/**
	 * 全司下所有省份当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceTodaySaleReport() throws Exception;

	/**
	 * 全司下所有省份当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceCurMonthSaleReport() throws Exception;
}

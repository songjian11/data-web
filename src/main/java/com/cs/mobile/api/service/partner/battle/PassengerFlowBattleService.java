package com.cs.mobile.api.service.partner.battle;

import com.cs.mobile.api.model.partner.battle.BattleReportResult;

public interface PassengerFlowBattleService {
	/**
	 * 门店下所有小店当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComTodayPFReport(String storeId, Integer isCompare) throws Exception;

	/**
	 * 门店下所有小店当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComCurMonthPFReport(String storeId, Integer isCompare) throws Exception;

	/**
	 * 小店下所有大类当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptTodayPFReport(String storeId, String comId, Integer isCompare) throws Exception;

	/**
	 * 小店下所有大类当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptCurMonthPFReport(String storeId, String comId, Integer isCompare) throws Exception;

	/**
	 * 区域下所有门店当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreTodayPFReport(String areaId) throws Exception;

	/**
	 * 区域下所有门店当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreCurMonthPFReport(String areaId) throws Exception;

	/**
	 * 省份下所有区域当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaTodayPFReport(String provinceId) throws Exception;

	/**
	 * 省份下所有区域当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaCurMonthPFReport(String provinceId) throws Exception;

	/**
	 * 全司下所有省份当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceTodayPFReport() throws Exception;

	/**
	 * 全司下所有省份当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceCurMonthPFReport() throws Exception;

}

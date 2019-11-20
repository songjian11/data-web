package com.cs.mobile.api.service.partner.battle.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.common.CommonDao;
import com.cs.mobile.api.dao.partner.battle.pf.ProvincePFBattleDao;
import com.cs.mobile.api.dao.partner.battle.pf.StorePFBattleDao;
import com.cs.mobile.api.dao.partner.battle.pf.ComPFBattleDao;
import com.cs.mobile.api.dao.partner.battle.pf.EnterprisePFBattleDao;
import com.cs.mobile.api.dao.partner.battle.pf.AreaPFBattleDao;
import com.cs.mobile.api.dao.partner.goal.GoalDao;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.partner.battle.BaseReport;
import com.cs.mobile.api.model.partner.battle.BattleReportResult;
import com.cs.mobile.api.model.partner.battle.RankReport;
import com.cs.mobile.api.model.partner.battle.TimeLineReport;
import com.cs.mobile.api.service.partner.battle.BattleAbstractService;
import com.cs.mobile.api.service.partner.battle.PassengerFlowBattleService;

@Service
public class PassengerFlowBattleServiceImpl extends BattleAbstractService implements PassengerFlowBattleService {
	@Autowired
	StorePFBattleDao storePFBattleDao;
	@Autowired
	ComPFBattleDao comPFBattleDao;
	@Autowired
	AreaPFBattleDao areaPFBattleDao;
	@Autowired
	ProvincePFBattleDao provincePFBattleDao;
	@Autowired
	EnterprisePFBattleDao enterprisePFBattleDao;
	@Autowired
	GoalDao goalDao;
	@Autowired
	CommonDao commonDao;
	@Value("${store.db.zyppdb.userName}")
	private String userName;
	@Value("${store.db.zyppdb.password}")
	private String password;
	@Value("${store.db.zyppdb.sid}")
	private String sid;
	@Value("${store.db.zyppdb.host}")
	private String host;
	@Value("${store.db.zyppdb.port}")
	private String port;
	@Autowired
	DruidProperties druidProperties;

	/**
	 * 门店下所有小店当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComTodayPFReport(String storeId, Integer isCompare) throws Exception {
		// 当日门店下所有小店实时客流
		List<BaseReport> todaySaleList = storePFBattleDao.getTodayPFList(storeId);
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getStoreLastYearDayList(storeId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		// 客流没有目标,因此传入goalMap为null
		BattleReportResult battleReportResult = this.todaySaleReportCore(2, todaySaleList, lastYearDayList, timeLineMap,
				null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 门店下所有小店当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComCurMonthPFReport(String storeId, Integer isCompare) throws Exception {
		// 当月门店下所有小店历史客流
		List<BaseReport> curMonthHistorytList = storePFBattleDao.getCurMonthPFList(storeId);
		// 当日门店下所有小店实时客流
		List<BaseReport> todaySaleList = storePFBattleDao.getTodayPFList(storeId);
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getStoreLastYearMonthList(storeId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.monthSaleReportCore(2, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 小店下所有大类当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptTodayPFReport(String storeId, String comId, Integer isCompare) throws Exception {
		// 当日小店下所有大类实时客流
		List<BaseReport> todaySaleList = comPFBattleDao.getTodayPFList(storeId, comId);
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getComLastYearDayList(storeId, comId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.todaySaleReportCore(1, todaySaleList, lastYearDayList, timeLineMap,
				null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 小店下所有大类当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptCurMonthPFReport(String storeId, String comId, Integer isCompare)
			throws Exception {
		// 当月小店下所有大类历史客流
		List<BaseReport> curMonthHistorytList = comPFBattleDao.getCurMonthPFList(storeId, comId);
		// 当日小店下所有大类实时客流
		List<BaseReport> todaySaleList = comPFBattleDao.getTodayPFList(storeId, comId);
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getComLastYearMonthList(storeId, comId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.monthSaleReportCore(1, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 区域下所有门店当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreTodayPFReport(String areaId) throws Exception {
		// 当日区域下所有门店实时客流
		List<BaseReport> todaySaleList = areaPFBattleDao.getTodayPFList(areaId);
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getAreaLastYearDayList(areaId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.todaySaleReportCore(1, todaySaleList, lastYearDayList, timeLineMap,
				null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 区域下所有门店当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreCurMonthPFReport(String areaId) throws Exception {
		// 当月区域下所有门店历史客流
		List<BaseReport> curMonthHistorytList = areaPFBattleDao.getCurMonthPFList(areaId);
		// 当日区域下所有门店实时客流
		List<BaseReport> todaySaleList = areaPFBattleDao.getTodayPFList(areaId);
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getAreaLastYearMonthList(areaId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.monthSaleReportCore(1, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 省份下所有区域当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaTodayPFReport(String provinceId) throws Exception {
		// 当日省份下所有区域实时客流
		List<BaseReport> todaySaleList = provincePFBattleDao.getTodayPFList(provinceId);
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getProvinceLastYearDayList(provinceId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.todaySaleReportCore(1, todaySaleList, lastYearDayList, timeLineMap,
				null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 省份下所有区域当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaCurMonthPFReport(String provinceId) throws Exception {
		// 当月省份下所有区域历史客流
		List<BaseReport> curMonthHistorytList = provincePFBattleDao.getCurMonthPFList(provinceId);
		// 当日省份下所有区域实时客流
		List<BaseReport> todaySaleList = provincePFBattleDao.getTodayPFList(provinceId);
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getProvinceLastYearMonthList(provinceId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.monthSaleReportCore(1, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 全司下所有省份当天客流日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceTodayPFReport() throws Exception {
		// 当日全司下所有省份实时客流
		List<BaseReport> todaySaleList = enterprisePFBattleDao.getTodayPFList();
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getEnterpriseLastYearDayList();
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.todaySaleReportCore(1, todaySaleList, lastYearDayList, timeLineMap,
				null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 全司下所有省份当月客流月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceCurMonthPFReport() throws Exception {
		// 当月全司下所有省份历史客流
		List<BaseReport> curMonthHistorytList = enterprisePFBattleDao.getCurMonthPFList();
		// 当日全司下所有省份实时客流
		List<BaseReport> todaySaleList = enterprisePFBattleDao.getTodayPFList();
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getEnterpriseLastYearMonthList();
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		// 客流没有目标，因此goalMap传入null
		BattleReportResult battleReportResult = this.monthSaleReportCore(1, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, null, rankMap, "人", BigDecimal.ONE);
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	private List<BaseReport> getComLastYearMonthList(String storeId, String comId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.comPFBattleDao.getLastYearMonthList(storeId, comId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getComLastYearDayList(String storeId, String comId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.comPFBattleDao.getLastYearDayList(storeId, comId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getStoreLastYearMonthList(String storeId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.storePFBattleDao.getLastYearMonthList(storeId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getStoreLastYearDayList(String storeId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.storePFBattleDao.getLastYearDayList(storeId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getAreaLastYearMonthList(String areaId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.areaPFBattleDao.getLastYearMonthList(areaId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getAreaLastYearDayList(String areaId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.areaPFBattleDao.getLastYearDayList(areaId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getProvinceLastYearMonthList(String provinceId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.provincePFBattleDao.getLastYearMonthList(provinceId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getProvinceLastYearDayList(String provinceId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.provincePFBattleDao.getLastYearDayList(provinceId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getEnterpriseLastYearMonthList() throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.enterprisePFBattleDao.getLastYearMonthList();
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getEnterpriseLastYearDayList() throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.enterprisePFBattleDao.getLastYearDayList();
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}
}

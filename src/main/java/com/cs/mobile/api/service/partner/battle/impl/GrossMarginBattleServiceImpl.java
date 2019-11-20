package com.cs.mobile.api.service.partner.battle.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.common.CommonDao;
import com.cs.mobile.api.dao.partner.battle.gm.ProvinceGMBattleDao;
import com.cs.mobile.api.dao.partner.battle.gm.StoreGMBattleDao;
import com.cs.mobile.api.dao.partner.battle.gm.ComGMBattleDao;
import com.cs.mobile.api.dao.partner.battle.gm.EnterpriseGMBattleDao;
import com.cs.mobile.api.dao.partner.battle.gm.AreaGMBattleDao;
import com.cs.mobile.api.dao.partner.goal.GoalDao;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.goal.Goal;
import com.cs.mobile.api.model.partner.battle.BaseReport;
import com.cs.mobile.api.model.partner.battle.BattleReportResult;
import com.cs.mobile.api.model.partner.battle.RankReport;
import com.cs.mobile.api.model.partner.battle.TimeLineReport;
import com.cs.mobile.api.service.partner.battle.BattleAbstractService;
import com.cs.mobile.api.service.partner.battle.GrossMarginBattleService;

@Service
public class GrossMarginBattleServiceImpl extends BattleAbstractService implements GrossMarginBattleService {
	@Autowired
	GoalDao goalDao;
	@Autowired
	CommonDao commonDao;
	@Autowired
	StoreGMBattleDao storeGMBattleDao;
	@Autowired
	ComGMBattleDao comGMBattleDao;
	@Autowired
	AreaGMBattleDao areaGMBattleDao;
	@Autowired
	ProvinceGMBattleDao provinceGMBattleDao;
	@Autowired
	EnterpriseGMBattleDao enterpriseGMBattleDao;
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
	 * 门店下所有小店当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComTodayGMReport(String storeId, Integer isCompare) throws Exception {
		// 当日门店下所有小店实时总毛利
		List<BaseReport> todaySaleList = storeGMBattleDao.getTodayGMList(storeId);
		// 获取门店下所有小店当月毛利目标
		List<Goal> saleGoalList = goalDao.getCurMonthComTGMGoal(storeId);
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getStoreLastYearDayList(storeId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		this.comGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.todaySaleReportCore(2, todaySaleList, lastYearDayList, timeLineMap,
				goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 门店下所有小店当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComCurMonthGMReport(String storeId, Integer isCompare) throws Exception {
		// 当月门店下所有小店历史毛利
		List<BaseReport> curMonthHistorytList = storeGMBattleDao.getCurMonthGMList(storeId);
		// 当日门店下所有小店实时总台毛利
		List<BaseReport> todaySaleList = storeGMBattleDao.getTodayGMList(storeId);
		// 获取门店下所有小店当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthComTGMGoal(storeId);
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getStoreLastYearMonthList(storeId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		this.comGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.monthSaleReportCore(2, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 小店下所有大类当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptTodayGMReport(String storeId, String comId, Integer isCompare) throws Exception {
		// 当日小店下所有大类实时总毛利
		List<BaseReport> todaySaleList = comGMBattleDao.getTodayGMList(storeId, comId);
		// 获取小店下所有大类当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthDeptTGMGoal(storeId, comId);
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getComLastYearDayList(storeId, comId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		this.deptGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.todaySaleReportCore(1, todaySaleList, lastYearDayList, timeLineMap,
				goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 小店下所有大类当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptCurMonthGMReport(String storeId, String comId, Integer isCompare)
			throws Exception {
		// 当月小店下所有大类历史毛利
		List<BaseReport> curMonthHistorytList = comGMBattleDao.getCurMonthGMList(storeId, comId);
		// 当日小店下所有大类实时总毛利
		List<BaseReport> todaySaleList = comGMBattleDao.getTodayGMList(storeId, comId);
		// 获取小店下所有大类当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthDeptTGMGoal(storeId, comId);
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getComLastYearMonthList(storeId, comId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		this.deptGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.monthSaleReportCore(1, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 区域下所有门店当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreTodayGMReport(String areaId) throws Exception {
		// 当日区域下所有门店实时总毛利
		List<BaseReport> todaySaleList = areaGMBattleDao.getTodayGMList(areaId);
		// 获取区域下所有门店当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthStoreTGMGoal(areaId);
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getAreaLastYearDayList(areaId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		this.storeGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.todaySaleReportCore(3, todaySaleList, lastYearDayList, timeLineMap,
				goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 区域下所有门店当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreCurMonthGMReport(String areaId) throws Exception {
		// 当月区域下所有门店历史毛利
		List<BaseReport> curMonthHistorytList = areaGMBattleDao.getCurMonthGMList(areaId);
		// 当日区域下所有门店实时总毛利
		List<BaseReport> todaySaleList = areaGMBattleDao.getTodayGMList(areaId);
		// 获取区域下所有门店当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthStoreTGMGoal(areaId);
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getAreaLastYearMonthList(areaId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		this.storeGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.monthSaleReportCore(3, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 省份下所有区域当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaTodayGMReport(String provinceId) throws Exception {
		// 当日省份下所有区域实时总毛利
		List<BaseReport> todaySaleList = provinceGMBattleDao.getTodayGMList(provinceId);
		// 获取省份下所有区域当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthAreaTGMGoal(provinceId);
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getProvinceLastYearDayList(provinceId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		this.areaGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.todaySaleReportCore(4, todaySaleList, lastYearDayList, timeLineMap,
				goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 省份下所有区域当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaCurMonthGMReport(String provinceId) throws Exception {
		// 当月省份下所有区域历史毛利
		List<BaseReport> curMonthHistorytList = provinceGMBattleDao.getCurMonthGMList(provinceId);
		// 当日省份下所有区域实时总毛利
		List<BaseReport> todaySaleList = provinceGMBattleDao.getTodayGMList(provinceId);
		// 获取省份下所有区域当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthAreaTGMGoal(provinceId);
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getProvinceLastYearMonthList(provinceId);
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		this.areaGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.monthSaleReportCore(4, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 全司下所有省份当天毛利日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceTodayGMReport() throws Exception {
		// 当日全司下所有省份实时总毛利
		List<BaseReport> todaySaleList = enterpriseGMBattleDao.getTodayGMList();
		// 获取全司下所有省份当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthProvinceTGMGoal();
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getEnterpriseLastYearDayList();
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		this.provinceGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.todaySaleReportCore(5, todaySaleList, lastYearDayList, timeLineMap,
				goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 全司下所有省份当月毛利月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceCurMonthGMReport() throws Exception {
		// 当月全司下所有省份历史毛利
		List<BaseReport> curMonthHistorytList = enterpriseGMBattleDao.getCurMonthGMList();
		// 当日全司下所有省份实时总毛利
		List<BaseReport> todaySaleList = enterpriseGMBattleDao.getTodayGMList();
		// 获取全司下所有省份当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthProvinceTGMGoal();
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getEnterpriseLastYearMonthList();
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		this.provinceGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.monthSaleReportCore(5, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	private List<BaseReport> getComLastYearMonthList(String storeId, String comId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.comGMBattleDao.getLastYearMonthList(storeId, comId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getComLastYearDayList(String storeId, String comId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.comGMBattleDao.getLastYearDayList(storeId, comId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getStoreLastYearMonthList(String storeId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.storeGMBattleDao.getLastYearMonthList(storeId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getStoreLastYearDayList(String storeId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.storeGMBattleDao.getLastYearDayList(storeId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getAreaLastYearMonthList(String areaId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.areaGMBattleDao.getLastYearMonthList(areaId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getAreaLastYearDayList(String areaId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.areaGMBattleDao.getLastYearDayList(areaId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getProvinceLastYearMonthList(String provinceId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.provinceGMBattleDao.getLastYearMonthList(provinceId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getProvinceLastYearDayList(String provinceId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.provinceGMBattleDao.getLastYearDayList(provinceId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getEnterpriseLastYearMonthList() throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.enterpriseGMBattleDao.getLastYearMonthList();
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getEnterpriseLastYearDayList() throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.enterpriseGMBattleDao.getLastYearDayList();
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}
}

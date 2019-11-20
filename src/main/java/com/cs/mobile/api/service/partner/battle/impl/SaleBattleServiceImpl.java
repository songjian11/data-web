package com.cs.mobile.api.service.partner.battle.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.common.CommonDao;
import com.cs.mobile.api.dao.partner.battle.sale.ProvinceSaleBattleDao;
import com.cs.mobile.api.dao.partner.battle.sale.StoreSaleBattleDao;
import com.cs.mobile.api.dao.partner.battle.sale.ComSaleBattleDao;
import com.cs.mobile.api.dao.partner.battle.sale.EnterpriseSaleBattleDao;
import com.cs.mobile.api.dao.partner.battle.sale.AreaSaleBattleDao;
import com.cs.mobile.api.dao.partner.goal.GoalDao;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.goal.Goal;
import com.cs.mobile.api.model.partner.battle.BaseReport;
import com.cs.mobile.api.model.partner.battle.BattleReportResult;
import com.cs.mobile.api.model.partner.battle.RankReport;
import com.cs.mobile.api.model.partner.battle.TimeLineReport;
import com.cs.mobile.api.service.partner.battle.BattleAbstractService;
import com.cs.mobile.api.service.partner.battle.SaleBattleService;

@Service
public class SaleBattleServiceImpl extends BattleAbstractService implements SaleBattleService {
	@Autowired
	GoalDao goalDao;
	@Autowired
	CommonDao commonDao;
	@Autowired
	StoreSaleBattleDao storeSaleBattleDao;
	@Autowired
	ComSaleBattleDao comSaleBattleDao;
	@Autowired
	AreaSaleBattleDao areaSaleBattleDao;
	@Autowired
	ProvinceSaleBattleDao provinceSaleBattleDao;
	@Autowired
	EnterpriseSaleBattleDao enterpriseSaleBattleDao;
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
	 * 门店下所有小店当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComTodaySaleReport(String storeId, Integer isCompare) throws Exception {
		// 当日门店下所有小店实时销售
		List<BaseReport> todaySaleList = storeSaleBattleDao.getTodaySaleList(storeId);
		// 获取门店下所有小店当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoalListByStoreId(storeId, "销售");
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
	 * 门店下所有小店当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getComCurMonthSaleReport(String storeId, Integer isCompare) throws Exception {
		// 当月门店下所有小店历史销售
		List<BaseReport> curMonthHistorytList = storeSaleBattleDao.getCurMonthSaleList(storeId);
		// 当日门店下所有小店实时销售
		List<BaseReport> todaySaleList = storeSaleBattleDao.getTodaySaleList(storeId);
		// 获取门店下所有小店当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoalListByStoreId(storeId, "销售");
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
	 * 小店下所有大类当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptTodaySaleReport(String storeId, String comId, Integer isCompare) throws Exception {
		// 当日小店下所有大类实时销售
		List<BaseReport> todaySaleList = comSaleBattleDao.getTodaySaleList(storeId, comId);
		// 获取门店下所有小店当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoalListByComId(storeId, comId, "销售");
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
	 * 小店下所有大类当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getDeptCurMonthSaleReport(String storeId, String comId, Integer isCompare)
			throws Exception {
		// 当月小店下所有大类历史销售
		List<BaseReport> curMonthHistorytList = comSaleBattleDao.getCurMonthSaleList(storeId, comId);
		// 当日小店下所有大类实时销售
		List<BaseReport> todaySaleList = comSaleBattleDao.getTodaySaleList(storeId, comId);
		// 获取小店下所有大类当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoalListByComId(storeId, comId, "销售");
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
	 * 区域下所有门店当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreTodaySaleReport(String areaId) throws Exception {
		// 当日区域下所有门店实时销售
		List<BaseReport> todaySaleList = areaSaleBattleDao.getTodaySaleList(areaId);
		// 获取区域下所有门店当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoalByAreaId(areaId, "销售");
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
	 * 区域下所有门店当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getStoreCurMonthSaleReport(String areaId) throws Exception {
		// 当月区域下所有门店历史销售
		List<BaseReport> curMonthHistorytList = areaSaleBattleDao.getCurMonthSaleList(areaId);
		// 当日区域下所有门店实时销售
		List<BaseReport> todaySaleList = areaSaleBattleDao.getTodaySaleList(areaId);
		// 获取区域下所有门店当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoalByAreaId(areaId, "销售");
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
	 * 省份下所有区域当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaTodaySaleReport(String provinceId) throws Exception {
		// 当日省份下所有区域实时销售
		List<BaseReport> todaySaleList = provinceSaleBattleDao.getTodaySaleList(provinceId);
		// 获取省份下所有区域当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoalByPvid(provinceId, "销售");
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
	 * 省份下所有区域当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getAreaCurMonthSaleReport(String provinceId) throws Exception {
		// 当月省份下所有区域历史销售
		List<BaseReport> curMonthHistorytList = provinceSaleBattleDao.getCurMonthSaleList(provinceId);
		// 当日省份下所有区域实时销售
		List<BaseReport> todaySaleList = provinceSaleBattleDao.getTodaySaleList(provinceId);
		// 获取省份下所有区域当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoalByPvid(provinceId, "销售");
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
	 * 全司下所有省份当天销售日报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceTodaySaleReport() throws Exception {
		// 当日全司下所有省份实时销售
		List<BaseReport> todaySaleList = enterpriseSaleBattleDao.getTodaySaleList();
		// 获取全司下所有省份当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoal("销售");
		// 去年今日列表
		List<BaseReport> lastYearDayList = this.getEnterpriseLastYearDayList();
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		this.provinceGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.todaySaleReportCore(4, todaySaleList, lastYearDayList, timeLineMap,
				goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	/**
	 * 全司下所有省份当月销售月报
	 * 
	 * @return
	 * @throws Exception
	 */
	public BattleReportResult getProvinceCurMonthSaleReport() throws Exception {
		// 当月全司下所有省份历史销售
		List<BaseReport> curMonthHistorytList = enterpriseSaleBattleDao.getCurMonthSaleList();
		// 当日全司下所有省份实时销售
		List<BaseReport> todaySaleList = enterpriseSaleBattleDao.getTodaySaleList();
		// 获取全司下所有省份当月目标
		List<Goal> saleGoalList = goalDao.getCurMonthGoal("销售");
		// 去年本月列表
		List<BaseReport> lastYearMonthList = this.getEnterpriseLastYearMonthList();
		Map<String, TimeLineReport> timeLineMap = new HashMap<String, TimeLineReport>();
		Map<String, RankReport> rankMap = new HashMap<String, RankReport>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> todaySaleMap = new HashMap<String, BigDecimal>();
		this.provinceGoalHandler(saleGoalList, goalMap);
		BattleReportResult battleReportResult = this.monthSaleReportCore(4, todaySaleList, lastYearMonthList,
				todaySaleMap, timeLineMap, curMonthHistorytList, goalMap, rankMap, "万元", new BigDecimal(10000));
		this.raitoHandler(battleReportResult);
		return battleReportResult;
	}

	private List<BaseReport> getComLastYearMonthList(String storeId, String comId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.comSaleBattleDao.getLastYearMonthList(storeId, comId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getComLastYearDayList(String storeId, String comId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.comSaleBattleDao.getLastYearDayList(storeId, comId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getStoreLastYearMonthList(String storeId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.storeSaleBattleDao.getLastYearMonthList(storeId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getStoreLastYearDayList(String storeId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.storeSaleBattleDao.getLastYearDayList(storeId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getAreaLastYearMonthList(String areaId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.areaSaleBattleDao.getLastYearMonthList(areaId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getAreaLastYearDayList(String areaId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.areaSaleBattleDao.getLastYearDayList(areaId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getProvinceLastYearMonthList(String provinceId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.provinceSaleBattleDao.getLastYearMonthList(provinceId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getProvinceLastYearDayList(String provinceId) throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.provinceSaleBattleDao.getLastYearDayList(provinceId);
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getEnterpriseLastYearMonthList() throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.enterpriseSaleBattleDao.getLastYearMonthList();
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}

	private List<BaseReport> getEnterpriseLastYearDayList() throws Exception {
		try {
			this.changeDataSource(druidProperties, host, port, sid, userName, password);
			return this.enterpriseSaleBattleDao.getLastYearDayList();
		} catch (Exception e) {
			throw e;
		} finally {
			DataSourceHolder.clearDataSource();
		}
	}
}

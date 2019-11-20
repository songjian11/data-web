package com.cs.mobile.api.service.partner.battle;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cs.mobile.api.datasource.DataSourceBuilder;
import com.cs.mobile.api.datasource.DataSourceHolder;
import com.cs.mobile.api.datasource.DruidProperties;
import com.cs.mobile.api.model.goal.Goal;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;
import com.cs.mobile.api.model.partner.battle.BaseReport;
import com.cs.mobile.api.model.partner.battle.BattleReportResult;
import com.cs.mobile.api.model.partner.battle.RankReport;
import com.cs.mobile.api.model.partner.battle.TimeLineReport;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.OperationUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 战报抽象对象
 * 
 * @author wells
 * @date 2019年1月5日
 */
@Slf4j
public abstract class BattleAbstractService {
	/**
	 * 月报核心处理
	 * 
	 * @param todaySaleList
	 * @param todaySaleMap
	 * @param timeLineMap
	 * @param curMonthHistorytList
	 * @param goalMap
	 * @param rankMap
	 * @param lastYearSaleTotal
	 * @param lastMonthSaleTotal
	 * @param isCompare
	 * @return
	 * @throws Exception
	 */
	protected BattleReportResult monthSaleReportCore(int type, List<BaseReport> todaySaleList,
			List<BaseReport> lastYearMonthList, Map<String, BigDecimal> todaySaleMap,
			Map<String, TimeLineReport> timeLineMap, List<BaseReport> curMonthHistorytList,
			Map<String, BigDecimal> goalMap, Map<String, RankReport> rankMap, String unit, BigDecimal unitDivisor)
			throws Exception {
		BigDecimal curMonthSaleTotal = BigDecimal.ZERO;// 当月总销售额
		BigDecimal todaySaleTotal = BigDecimal.ZERO;// 当天实时总销售
		Map<String, BigDecimal> lastYearMonthMap = new HashMap<String, BigDecimal>();
		this.lastYearListHandler(lastYearMonthList, lastYearMonthMap);
		for (BaseReport todaySale : todaySaleList) {
			if (type == 1) {// 查看小店下所有大类的数据
				todaySaleTotal = todaySaleTotal.add(todaySale.getValue());
			} else if (type == 2) {// 查看大店下所有小店的数据
				if ("ALL".equals(todaySale.getOrgId())) {// 累计当天销售额即为整个小店/大店的当天总销售
					todaySaleTotal = todaySaleTotal.add(todaySale.getValue());
				}
			} else {// 区域下所有大店、省份下所有区域、全司下所有省份
				todaySaleTotal = todaySaleTotal.add(todaySale.getValue());
			}
			if (todaySaleMap.get(todaySale.getOrgId()) != null) {
				todaySaleMap.put(todaySale.getOrgId(),
						todaySaleMap.get(todaySale.getOrgId()).add(todaySale.getValue()));
			} else {
				todaySaleMap.put(todaySale.getOrgId(), todaySale.getValue());
			}
		}
		// 封装今日销售总额
		TimeLineReport todayTimeLineReport = new TimeLineReport();
		String today = DateUtil.getDateFormat(DateUtil.getNowDate(), "yyyy-MM-dd");
		todayTimeLineReport.setTime(today);
		todayTimeLineReport.setValue(todaySaleTotal);
		timeLineMap.put(today, todayTimeLineReport);
		BigDecimal processValue = BigDecimal.ZERO;
		BigDecimal targetValue = BigDecimal.ZERO;
		// 今日累计的总额需要累计到月累计中
		curMonthSaleTotal = curMonthSaleTotal.add(todaySaleTotal);
		// 处理历史销售数据
		for (BaseReport battleBaseReport : curMonthHistorytList) {
			targetValue = BigDecimal.ZERO;
			BigDecimal lastYearMonth = lastYearMonthMap.get(battleBaseReport.getOrgId());
			if (type == 1) {// 查看小店下所有大类的数据
				curMonthSaleTotal = curMonthSaleTotal.add(battleBaseReport.getValue());
				this.timeLineReportHandler(timeLineMap, battleBaseReport);
			} else if (type == 2) {// 查看大店下所有小店的数据
				if ("ALL".equals(battleBaseReport.getOrgId())) {// 累计当天销售额即为整个小店/大店的当天总销售
					curMonthSaleTotal = curMonthSaleTotal.add(battleBaseReport.getValue());
					if ("整店".equals(battleBaseReport.getOrgName())) {// 整店只有一个点（ALL：整店），所以也需要加入到时间轴数据中
						this.timeLineReportHandler(timeLineMap, battleBaseReport);
					}
				} else {// 封装以时间为维度的timeLineReport,需要排除ALL
					this.timeLineReportHandler(timeLineMap, battleBaseReport);
				}
			} else {// 区域下所有大店、省份下所有区域、全司下所有省份
				curMonthSaleTotal = curMonthSaleTotal.add(battleBaseReport.getValue());
				this.timeLineReportHandler(timeLineMap, battleBaseReport);
			}
			// 封装以小店为维度的RankReport
			processValue = battleBaseReport.getValue()
					.add(todaySaleMap.get(battleBaseReport.getOrgId()) == null ? BigDecimal.ZERO
							: todaySaleMap.get(battleBaseReport.getOrgId()));
			if (goalMap != null && goalMap.get(battleBaseReport.getOrgId()) != null) {
				targetValue = goalMap.get(battleBaseReport.getOrgId())
						.divide(new BigDecimal(DateUtil.getCurrentMonthTotalDay()), 2, BigDecimal.ROUND_HALF_UP)
						.multiply(new BigDecimal(DateUtil.getCurrentTotalDay()));
			}
			this.rankReportHandler(rankMap, battleBaseReport, processValue, targetValue, lastYearMonth);
		}
		return this.reportHandler(timeLineMap, rankMap, curMonthSaleTotal, unit, unitDivisor);
	}

	/**
	 * 日报核心处理
	 * 
	 * @param todaySaleList
	 * @param timeLineMap
	 * @param goalMap
	 * @param rankMap
	 * @param lastYearDayTotal
	 * @param lastMonthDayTotal
	 * @param isCompare
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected BattleReportResult todaySaleReportCore(int type, List<BaseReport> todaySaleList,
			List<BaseReport> lastYearDayList, Map<String, TimeLineReport> timeLineMap, Map<String, BigDecimal> goalMap,
			Map<String, RankReport> rankMap, String unit, BigDecimal unitDivisor) throws Exception {
		BigDecimal todaySaleTotal = BigDecimal.ZERO;// 当天实时总销售
		Map<String, BigDecimal> lastYearDayMap = new HashMap<String, BigDecimal>();
		this.lastYearListHandler(lastYearDayList, lastYearDayMap);
		for (BaseReport battleBaseReport : todaySaleList) {
			BigDecimal lastYearDay = BigDecimal.ZERO;
			if (lastYearDayMap.get(battleBaseReport.getOrgId()) != null) {
				lastYearDay = lastYearDayMap.get(battleBaseReport.getOrgId());
			}
			if (type == 1) {// 查看小店下所有大类的数据
				todaySaleTotal = todaySaleTotal.add(battleBaseReport.getValue());
				this.timeLineReportHandler(timeLineMap, battleBaseReport);
			} else if (type == 2) {// 查看大店下所有小店的数据
				if ("ALL".equals(battleBaseReport.getOrgId())) {// 累计当天销售额即为整个大店的当天总销售
					todaySaleTotal = todaySaleTotal.add(battleBaseReport.getValue());
					if ("整店".equals(battleBaseReport.getOrgName())) {// 整店只有一个小店（ALL:整店），所以需要加入到时间轴数据中
						this.timeLineReportHandler(timeLineMap, battleBaseReport);
					}
				} else {// 封装以时间为维度的timeLineReport,需要排除ALL
					this.timeLineReportHandler(timeLineMap, battleBaseReport);
				}
			} else {// 区域下所有大店、省份下所有区域、全司下所有省份
				todaySaleTotal = todaySaleTotal.add(battleBaseReport.getValue());
				this.timeLineReportHandler(timeLineMap, battleBaseReport);
			}
			// 封装以小店为维度的RankReport
			BigDecimal processValue = BigDecimal.ZERO;
			BigDecimal targetValue = BigDecimal.ZERO;
			processValue = battleBaseReport.getValue();
			if (goalMap != null && goalMap.get(battleBaseReport.getOrgId()) != null) {
				targetValue = goalMap.get(battleBaseReport.getOrgId())
						.divide(new BigDecimal(DateUtil.getCurrentMonthTotalDay()), 2, BigDecimal.ROUND_HALF_UP);
			}
			this.rankReportHandler(rankMap, battleBaseReport, processValue, targetValue, lastYearDay);
		}
		return this.reportHandler(timeLineMap, rankMap, todaySaleTotal, unit, unitDivisor);

	}

	/**
	 * 历史客流数据处理
	 * 
	 * @param saleGoalList
	 * @param goalMap
	 * @author wells
	 * @date 2019年4月5日
	 */
	protected void lastYearListHandler(List<BaseReport> lastYearDayList, Map<String, BigDecimal> lastYearDayMap) {
		if (lastYearDayList != null && lastYearDayList.size() > 0) {
			lastYearDayList.forEach(baseReport -> {
				if (lastYearDayMap.get(baseReport.getOrgId()) != null) {
					lastYearDayMap.put(baseReport.getOrgId(),
							lastYearDayMap.get(baseReport.getOrgId()).add(baseReport.getValue()));
				} else {
					lastYearDayMap.put(baseReport.getOrgId(), baseReport.getValue());
				}
			});
		}
	}

	/**
	 * 小店目标数据处理
	 * 
	 * @param saleGoalList
	 * @param goalMap
	 */
	protected void comGoalHandler(List<Goal> saleGoalList, Map<String, BigDecimal> goalMap) {
		saleGoalList.forEach(goal -> {
			if (goalMap.get(goal.getComId()) != null) {
				goalMap.put(goal.getComId(), goalMap.get(goal.getComId()).add(goal.getSubValues()));
			} else {
				goalMap.put(goal.getComId(), goal.getSubValues());
			}
		});
	}

	/**
	 * 大类目标数据处理
	 * 
	 * @param saleGoalList
	 * @param goalMap
	 */
	protected void deptGoalHandler(List<Goal> saleGoalList, Map<String, BigDecimal> goalMap) {
		saleGoalList.forEach(goal -> {
			if (goalMap.get(goal.getDeptId()) != null) {
				goalMap.put(goal.getDeptId(), goalMap.get(goal.getDeptId()).add(goal.getSubValues()));
			} else {
				goalMap.put(goal.getDeptId(), goal.getSubValues());
			}
		});
	}

	/**
	 * 门店目标数据处理
	 * 
	 * @param saleGoalList
	 * @param goalMap
	 */
	protected void storeGoalHandler(List<Goal> saleGoalList, Map<String, BigDecimal> goalMap) {
		saleGoalList.forEach(goal -> {
			if (goalMap.get(goal.getStoreId()) != null) {
				goalMap.put(goal.getStoreId(), goalMap.get(goal.getStoreId()).add(goal.getSubValues()));
			} else {
				goalMap.put(goal.getStoreId(), goal.getSubValues());
			}
		});
	}

	/**
	 * 区域目标数据处理
	 * 
	 * @param saleGoalList
	 * @param goalMap
	 */
	protected void areaGoalHandler(List<Goal> saleGoalList, Map<String, BigDecimal> goalMap) {
		saleGoalList.forEach(goal -> {
			if (goalMap.get(goal.getAreaId()) != null) {
				goalMap.put(goal.getAreaId(), goalMap.get(goal.getAreaId()).add(goal.getSubValues()));
			} else {
				goalMap.put(goal.getAreaId(), goal.getSubValues());
			}
		});
	}

	/**
	 * 省份目标数据处理
	 * 
	 * @param saleGoalList
	 * @param goalMap
	 */
	protected void provinceGoalHandler(List<Goal> saleGoalList, Map<String, BigDecimal> goalMap) {
		saleGoalList.forEach(goal -> {
			if (goalMap.get(goal.getProvinceId()) != null) {
				goalMap.put(goal.getProvinceId(), goalMap.get(goal.getProvinceId()).add(goal.getSubValues()));
			} else {
				goalMap.put(goal.getProvinceId(), goal.getSubValues());
			}
		});
	}

	/**
	 * 时间轴数据处理
	 * 
	 * @param timeLineMap
	 * @param battleBaseReport
	 */
	private void timeLineReportHandler(Map<String, TimeLineReport> timeLineMap, BaseReport battleBaseReport) {
		// 封装以时间为维度的timeLineReport
		TimeLineReport timeLineReport = new TimeLineReport();
		if (timeLineMap.get(battleBaseReport.getTime()) != null) {
			timeLineReport = timeLineMap.get(battleBaseReport.getTime());
			timeLineReport.setValue(timeLineReport.getValue().add(battleBaseReport.getValue()));
		} else {
			timeLineReport.setTime(battleBaseReport.getTime());
			timeLineReport.setValue(battleBaseReport.getValue());
			timeLineMap.put(battleBaseReport.getTime(), timeLineReport);
		}
	}

	/**
	 * 排名数据处理
	 * 
	 * @param rankMap
	 * @param battleBaseReport
	 * @param processValue
	 * @param TargetValue
	 */
	private void rankReportHandler(Map<String, RankReport> rankMap, BaseReport battleBaseReport,
			BigDecimal processValue, BigDecimal targetValue, BigDecimal lastYear) {
		// 封装以小店为维度的RankReport
		RankReport rankReport = new RankReport();
		if (rankMap.get(battleBaseReport.getOrgId()) != null) {
			rankReport = rankMap.get(battleBaseReport.getOrgId());
			rankReport.setProcessValue(rankReport.getProcessValue().add(battleBaseReport.getValue()));
		} else {
			rankReport.setCode(battleBaseReport.getOrgId());
			rankReport.setName(battleBaseReport.getOrgName());
			rankReport.setProcessValue(processValue);
			rankReport.setTargetValue(targetValue);
			rankReport.setCompareValue(lastYear == null ? BigDecimal.ZERO : lastYear);
			rankMap.put(battleBaseReport.getOrgId(), rankReport);
		}
	}

	/**
	 * 报表组装处理
	 * 
	 * @param timeLineMap
	 * @param rankMap
	 * @param curTotal
	 * @param lastYearTotal
	 * @param lastMonthTotal
	 * @param isCompare
	 * @return
	 * @throws Exception
	 */
	private BattleReportResult reportHandler(Map<String, TimeLineReport> timeLineMap, Map<String, RankReport> rankMap,
			BigDecimal curTotal, String unit, BigDecimal divisor) throws Exception {
		BattleReportResult battleReportResult = new BattleReportResult();
		// 循环从timeLineMap取出timeLineReport 并且排序
		List<TimeLineReport> timeLineReportList = Lists.newArrayList();
		BigDecimal timeLineReportTotal = BigDecimal.ZERO;
		for (Map.Entry<String, TimeLineReport> entry : timeLineMap.entrySet()) {
			TimeLineReport timeLineReport = entry.getValue();
			timeLineReport.setUnit(unit);
			timeLineReport.setValue(timeLineReport.getValue().divide(divisor).setScale(2, BigDecimal.ROUND_HALF_UP));
			timeLineReportList.add(timeLineReport);
			timeLineReportTotal = timeLineReportTotal.add(timeLineReport.getValue());
		}
		List<TimeLineReport> timeLineReportListResult = timeLineReportList.stream()
				.sorted(Comparator.comparing(TimeLineReport::getCompare)).collect(Collectors.toList());// 按照时间排序
		// 循环从rankMap取出RankReport 并且排序
		List<RankReport> rankReportList = Lists.newArrayList();
		BigDecimal lastYearTotal = BigDecimal.ZERO;
		BigDecimal curTotalGoal = BigDecimal.ZERO;
		for (Map.Entry<String, RankReport> entry : rankMap.entrySet()) {
			RankReport rankReport = entry.getValue();
			if (!"ALL".equals(entry.getKey()) || "整店".equals(rankReport.getName())) {// 累计历史金额的时候需要排除ALL的，整店不排除
				lastYearTotal = lastYearTotal.add(rankReport.getCompareValue());
				curTotalGoal = curTotalGoal.add(rankReport.getTargetValue());
			}
			rankReport.setUnit(unit);
			rankReport
					.setTargetValue(rankReport.getTargetValue().divide(divisor).setScale(2, BigDecimal.ROUND_HALF_UP));
			rankReport.setProcessValue(
					rankReport.getProcessValue().divide(divisor).setScale(2, BigDecimal.ROUND_HALF_UP));
			rankReport.setCompareValue(
					rankReport.getCompareValue().divide(divisor).setScale(2, BigDecimal.ROUND_HALF_UP));
			rankReportList.add(rankReport);
		}
		List<RankReport> rankReportListResult = rankReportList.stream()
				.sorted(Comparator.comparing(RankReport::getProcessRatio).reversed()
						.thenComparing(Comparator.comparing(RankReport::getProcessValue).reversed()))
				.collect(Collectors.toList());// 按照进度百分比、进度值倒序，即值越大越排前
		battleReportResult.setRankReportList(rankReportListResult);
		battleReportResult.setTimeLineReportList(timeLineReportListResult);
		battleReportResult.setTimeLineReportTotal(timeLineReportTotal);
		battleReportResult.setTimeLineReportTotalUnit(unit);
		battleReportResult.setCurTotal(curTotal.divide(divisor).setScale(2, BigDecimal.ROUND_HALF_UP));
		battleReportResult.setLastYearTotal(lastYearTotal.divide(divisor).setScale(2, BigDecimal.ROUND_HALF_UP));
		battleReportResult.setCurTotalGoal(curTotalGoal.divide(divisor).setScale(2, BigDecimal.ROUND_HALF_UP));
		return battleReportResult;
	}

	protected void raitoHandler(BattleReportResult battleReportResult) throws Exception {
		/**
		 * 同比：（当日销售额-去年今日的销售额）/去年今日的销售额
		 * 
		 */
		BigDecimal yearRatio = BigDecimal.ZERO;
		yearRatio = OperationUtil
				.divideHandler(battleReportResult.getCurTotal().subtract(battleReportResult.getLastYearTotal()),
						battleReportResult.getLastYearTotal())
				.multiply(new BigDecimal(100));
		battleReportResult.setYearRatio(yearRatio);
	}

	/**
	 * 切换数据源
	 * 
	 * @param druidProperties
	 * @param host
	 * @param port
	 * @param sid
	 * @param userName
	 * @param password
	 * @throws Exception
	 * @author wells
	 * @date 2019年4月19日
	 */
	protected void changeDataSource(DruidProperties druidProperties, String host, String port, String sid,
			String userName, String password) throws Exception {
		GoodsDataSourceConfig goodsDataSourceConfig = new GoodsDataSourceConfig();
		goodsDataSourceConfig.setHost(host);
		goodsDataSourceConfig.setPort(port);
		goodsDataSourceConfig.setSid(sid);
		goodsDataSourceConfig.setStore(sid);
		DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(goodsDataSourceConfig, userName, password,
				druidProperties);
		DataSourceHolder.setDataSource(dataSourceBuilder);
	}
}

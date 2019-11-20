package com.cs.mobile.api.service.partner.progress;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cs.mobile.api.model.common.DeptInfo;
import com.cs.mobile.api.model.goal.GoalSummary;
import com.cs.mobile.api.model.goal.GoalValue;
import com.cs.mobile.api.model.partner.assess.AssessResult;
import com.cs.mobile.api.model.partner.battle.BaseReportGpExt;
import com.cs.mobile.api.model.partner.progress.AccountingItemResult;
import com.cs.mobile.api.model.partner.progress.AssessItemResult;
import com.cs.mobile.api.model.partner.progress.CostVal;
import com.cs.mobile.api.model.partner.progress.DeptStock;
import com.cs.mobile.api.model.partner.progress.DistributionValues;
import com.cs.mobile.api.model.partner.progress.IncomeConfig;
import com.cs.mobile.api.model.partner.progress.ProgressCoreData;
import com.cs.mobile.api.model.partner.progress.ProgressReport;
import com.cs.mobile.api.model.partner.progress.TurnOverDays;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.OperationUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ProgressAbstractService {
	protected AccountingItemResult loadCurMonthAccountingItem(ProgressCoreData data) throws Exception {
		ProgressReport sale = null;
		ProgressReport fontGp = null;
		ProgressReport afterGp = null;
		ProgressReport attract = null;
		ProgressReport countGp = null;
		ProgressReport manpower = null;
		ProgressReport depreciation = null;
		ProgressReport hydropower = null;
		ProgressReport lease = null;
		ProgressReport other = null;
		ProgressReport stock = null;
		ProgressReport checkProfit = null;
		ProgressReport excessProfit = null;
		ProgressReport shareVal = null;
		BigDecimal incomeCount = BigDecimal.ZERO;
		BigDecimal costCount = BigDecimal.ZERO;
		String unit = "万";
		sale = new ProgressReport.Builder().typeName("销售")
				.goalVal(OperationUtil.divideHandler(data.getCurSaleGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualSale(), new BigDecimal(10000))).unit("万").build();
		fontGp = new ProgressReport.Builder().typeName("前台毛利")
				.goalVal(OperationUtil.divideHandler(data.getCurFrontGpGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualFrontGp(), new BigDecimal(10000))).unit("万")
				.build();
		afterGp = new ProgressReport.Builder().typeName("后台毛利")
				.goalVal(OperationUtil.divideHandler(data.getCurAfterGpGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualAfterGp(), new BigDecimal(10000))).unit("万")
				.build();
		attract = new ProgressReport.Builder().typeName("招商")
				.goalVal(OperationUtil.divideHandler(data.getCurAttractGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualAttract(), new BigDecimal(10000))).unit("万")
				.build();
		countGp = new ProgressReport.Builder().typeName("总毛利")
				.goalVal(OperationUtil.divideHandler(data.getCurGpGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualTotalGp(), new BigDecimal(10000))).unit("万")
				.build();
		manpower = new ProgressReport.Builder().typeName("人力成本")
				.goalVal(OperationUtil.divideHandler(data.getCurCostGoalObject().getManpower(), new BigDecimal(10000)))
				.actualVal(
						OperationUtil.divideHandler(data.getCurCostGoalObject().getManpower(), new BigDecimal(10000)))
				.unit("万").build();
		depreciation = new ProgressReport.Builder().typeName("折旧")
				.goalVal(OperationUtil.divideHandler(data.getCurCostGoalObject().getDepreciation(),
						new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getCurCostGoalObject().getDepreciation(),
						new BigDecimal(10000)))
				.unit("万").build();
		hydropower = new ProgressReport.Builder().typeName("水电")
				.goalVal(
						OperationUtil.divideHandler(data.getCurCostGoalObject().getHydropower(), new BigDecimal(10000)))
				.actualVal(
						OperationUtil.divideHandler(data.getCurCostGoalObject().getHydropower(), new BigDecimal(10000)))
				.unit("万").build();
		lease = new ProgressReport.Builder().typeName("租赁")
				.goalVal(OperationUtil.divideHandler(data.getCurCostGoalObject().getLease(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getCurCostGoalObject().getLease(), new BigDecimal(10000)))
				.unit("万").build();
		other = new ProgressReport.Builder().typeName("其他")
				.goalVal(OperationUtil.divideHandler(data.getCurCostGoalObject().getOther(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getCurCostGoalObject().getOther(), new BigDecimal(10000)))
				.unit("万").build();
		stock = new ProgressReport.Builder().typeName("库存").goalVal(BigDecimal.ZERO)
				.actualVal(OperationUtil.divideHandler(data.getActualStock(), new BigDecimal(10000))).unit("万").build();
		checkProfit = new ProgressReport.Builder().typeName("考核利润")
				.goalVal(OperationUtil.divideHandler(data.getCurProfitGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualTotalProfit(), new BigDecimal(10000))).unit("万")
				.build();
		excessProfit = new ProgressReport.Builder().typeName("超额利润")
				.actualVal(OperationUtil.divideHandler(data.getExcessProfit(), new BigDecimal(10000))).unit("万")
				.build();
		shareVal = new ProgressReport.Builder().typeName("分享金额").actualVal(data.getShare()).unit("元").build();
		incomeCount = OperationUtil.divideHandler(data.getActualTotalGp(), new BigDecimal(10000));// 实际总收入=实际总毛利+实际招商收入（0）
		costCount = OperationUtil.divideHandler(data.getActualTotalCost(), new BigDecimal(10000));
		return new AccountingItemResult(sale, fontGp, afterGp, attract, countGp, manpower, depreciation, hydropower,
				lease, other, stock, checkProfit, excessProfit, shareVal, incomeCount, costCount, unit);
	}

	/**
	 * 组装考核表数据
	 * 
	 * @author wells
	 * @param goalSummary
	 * @param assessItemResult
	 * @return
	 * @time 2018年12月19日
	 */
	protected AccountingItemResult loadHistoryAccountingItem(GoalSummary goalSummary,
			AssessItemResult assessItemResult) {
		ProgressReport sale = null;
		ProgressReport fontGp = null;
		ProgressReport afterGp = null;
		ProgressReport attract = null;
		ProgressReport countGp = null;
		ProgressReport manpower = null;
		ProgressReport depreciation = null;
		ProgressReport hydropower = null;
		ProgressReport lease = null;
		ProgressReport other = null;
		ProgressReport stock = null;
		ProgressReport checkProfit = null;
		ProgressReport excessProfit = null;
		ProgressReport shareVal = null;
		BigDecimal incomeCount = BigDecimal.ZERO;
		BigDecimal costCount = BigDecimal.ZERO;
		String unit = "万";
		sale = new ProgressReport.Builder().typeName("销售")
				.goalVal(OperationUtil.divideHandler(goalSummary.getSaleTotalGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getSale(), new BigDecimal(10000))).unit("万")
				.build();
		fontGp = new ProgressReport.Builder().typeName("前台毛利")
				.goalVal(OperationUtil.divideHandler(goalSummary.getFrontGpTotalGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getFrontGp(), new BigDecimal(10000))).unit("万")
				.build();
		afterGp = new ProgressReport.Builder().typeName("后台毛利")
				.goalVal(OperationUtil.divideHandler(goalSummary.getAfterGpTotalGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getAfterGp(), new BigDecimal(10000))).unit("万")
				.build();
		attract = new ProgressReport.Builder().typeName("招商")
				.goalVal(OperationUtil.divideHandler(goalSummary.getAttractTotalGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getAttract(), new BigDecimal(10000))).unit("万")
				.build();
		BigDecimal actualTotalGp = assessItemResult.getAfterGp()
				.add(assessItemResult.getAfterGp().subtract(assessItemResult.getDcCost()));
		countGp = new ProgressReport.Builder().typeName("总毛利")
				.goalVal(
						OperationUtil
								.divideHandler(
										goalSummary.getFrontGpTotalGoal()
												.add(goalSummary.getAfterGpTotalGoal()
														.subtract(goalSummary.getDcTotalGoal())),
										new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(actualTotalGp, new BigDecimal(10000))).unit("万").build();
		manpower = new ProgressReport.Builder().typeName("人力成本")
				.goalVal(OperationUtil.divideHandler(goalSummary.getCostValObject().getManpower(),
						new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getManpowerCost(), new BigDecimal(10000)))
				.unit("万").build();
		depreciation = new ProgressReport.Builder().typeName("折旧")
				.goalVal(OperationUtil.divideHandler(goalSummary.getCostValObject().getDepreciation(),
						new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getDepreciationCost(), new BigDecimal(10000)))
				.unit("万").build();
		hydropower = new ProgressReport.Builder().typeName("水电")
				.goalVal(OperationUtil.divideHandler(goalSummary.getCostValObject().getHydropower(),
						new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getHydropowerCost(), new BigDecimal(10000)))
				.unit("万").build();
		lease = new ProgressReport.Builder().typeName("租赁")
				.goalVal(OperationUtil.divideHandler(goalSummary.getCostValObject().getLease(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getLeaseCost(), new BigDecimal(10000)))
				.unit("万").build();
		other = new ProgressReport.Builder().typeName("其他")
				.goalVal(OperationUtil.divideHandler(goalSummary.getCostValObject().getOther(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(assessItemResult.getOtherCost(), new BigDecimal(10000)))
				.unit("万").build();
		stock = new ProgressReport.Builder().typeName("库存").goalVal(BigDecimal.ZERO)
				.actualVal(OperationUtil.divideHandler(assessItemResult.getStockCost(), new BigDecimal(10000)))
				.unit("万").build();
		BigDecimal actualProfit = assessItemResult.getAfterGp()
				.add(assessItemResult.getAfterGp().subtract(assessItemResult.getDcCost()))
				.subtract(assessItemResult.getTotalCost());
		BigDecimal profitGoal = goalSummary.getFrontGpTotalGoal()
				.add(goalSummary.getAfterGpTotalGoal().subtract(goalSummary.getDcTotalGoal()
						.add(goalSummary.getAttractTotalGoal()).subtract(goalSummary.getCostTotalGoal())));
		checkProfit = new ProgressReport.Builder().typeName("考核利润")
				.goalVal(OperationUtil.divideHandler(profitGoal, new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(actualProfit, new BigDecimal(10000))).unit("万").build();
		excessProfit = new ProgressReport.Builder().typeName("超额利润")
				.actualVal(OperationUtil.divideHandler(actualProfit.subtract(profitGoal), new BigDecimal(10000)))
				.unit("万").build();
		shareVal = new ProgressReport.Builder().typeName("分享金额").actualVal(assessItemResult.getShareval()).unit("元")
				.build();
		incomeCount = OperationUtil.divideHandler(actualTotalGp.add(assessItemResult.getAttract()),
				new BigDecimal(10000));
		costCount = OperationUtil.divideHandler(assessItemResult.getTotalCost(), new BigDecimal(10000));
		return new AccountingItemResult(sale, fontGp, afterGp, attract, countGp, manpower, depreciation, hydropower,
				lease, other, stock, checkProfit, excessProfit, shareVal, incomeCount, costCount, unit);
	}

	protected void assessHandler(Map<String, AssessItemResult> assessItemResultMap,
			List<AssessResult> assessResultList) {
		assessResultList.stream().forEach(assessResult -> {
			AssessItemResult assessItemResult = new AssessItemResult();
			if (assessItemResultMap.get(assessResult.getResultYm()) != null) {
				assessItemResult = assessItemResultMap.get(assessResult.getResultYm());
			}
			this.loadAssessItemResult(assessResult, assessItemResult);
			if (assessItemResultMap.get(assessResult.getResultYm()) == null) {
				assessItemResultMap.put(assessResult.getResultYm(), assessItemResult);
			}
		});
	}

	protected void loadAssessItemResult(AssessResult assessResult, AssessItemResult assessItemResult) {
		if ("销售".equals(assessResult.getSubject())) {
			assessItemResult.setSale(assessResult.getSubValues());
		}
		if ("毛利额".equals(assessResult.getSubject())) {
			assessItemResult.setFrontGp(assessResult.getSubValues());
		}
		if ("后台".equals(assessResult.getSubject())) {
			assessItemResult.setAfterGp(assessResult.getSubValues());
		}
		if ("招商收入".equals(assessResult.getSubject())) {
			assessItemResult.setAttract(assessResult.getSubValues());
		}
		if ("DC成本".equals(assessResult.getSubject())) {
			assessItemResult.setDcCost(assessResult.getSubValues());
		}
		if ("其它费用".equals(assessResult.getSubject())) {
			assessItemResult.setOtherCost(assessResult.getSubValues());
		}
		if ("折旧费用".equals(assessResult.getSubject())) {
			assessItemResult.setDepreciationCost(assessResult.getSubValues());
		}
		if ("租赁费用".equals(assessResult.getSubject())) {
			assessItemResult.setLeaseCost(assessResult.getSubValues());
		}
		if ("销售-水电费用".equals(assessResult.getSubject())) {
			assessItemResult.setHydropowerCost(assessResult.getSubValues());
		}
		if ("销售-人力成本".equals(assessResult.getSubject())) {
			assessItemResult.setManpowerCost(assessResult.getSubValues());
		}
		if ("库存资金占用成本".equals(assessResult.getSubject())) {
			assessItemResult.setStockCost(assessResult.getSubValues());
		}
	}

	// 单个小店包括后勤小店的处理
	protected ProgressCoreData getProgressCoreData(String beginYmd, String endYmd,
			List<BaseReportGpExt> todayDeptSaleList, List<BaseReportGpExt> historyDeptSaleList,
			List<GoalValue> goalValueList, List<IncomeConfig> incomeCnfList, List<DeptStock> deptStockList,
			List<TurnOverDays> turnOverDaysList, BigDecimal distributionValue, List<DeptInfo> deptInfoList)
			throws Exception {
		Map<String, BaseReportGpExt> todayDeptSaleMap = new HashMap<String, BaseReportGpExt>();
		Map<String, IncomeConfig> incomeConfigMap = new HashMap<String, IncomeConfig>();
		Map<String, TurnOverDays> turnOverDaysMap = new HashMap<String, TurnOverDays>();
		Map<String, DeptInfo> deptMap = new HashMap<String, DeptInfo>();
		this.todaySaleHandler(todayDeptSaleList, todayDeptSaleMap);
		this.deptInfoHandler(deptInfoList, deptMap);
		incomeCnfList.stream().forEach(incomeConfig -> {
			incomeConfigMap.put(incomeConfig.getDeptId(), incomeConfig);
		});
		turnOverDaysList.stream().forEach(turnOverDays -> {
			turnOverDaysMap.put(turnOverDays.getDeptId(), turnOverDays);
		});
		// 调用核心处理，封装所需要的数据
		return this.progressReportCore(goalValueList, deptStockList, beginYmd, endYmd, historyDeptSaleList,
				todayDeptSaleMap, incomeConfigMap, turnOverDaysMap, distributionValue, deptMap);
	}

	// 多个小店的处理
	protected ProgressCoreData getMulitProgressCoreData(String beginYmd, String endYmd,
			List<BaseReportGpExt> todayDeptSaleList, List<BaseReportGpExt> historyDeptSaleList,
			List<GoalValue> goalValueList, List<IncomeConfig> incomeCnfList, List<DeptStock> deptStockList,
			List<TurnOverDays> turnOverDaysList, List<DistributionValues> dValueList, List<DeptInfo> deptInfoList)
			throws Exception {
		Map<String, List<BaseReportGpExt>> mulitHistoryDeptSaleListMap = new HashMap<String, List<BaseReportGpExt>>();
		Map<String, List<BaseReportGpExt>> mulitTodayDeptSaleListMap = new HashMap<String, List<BaseReportGpExt>>();
		Map<String, List<GoalValue>> mulitGoalValueListMap = new HashMap<String, List<GoalValue>>();
		Map<String, List<DeptStock>> mulitDeptStockListMap = new HashMap<String, List<DeptStock>>();
		Map<String, BigDecimal> mulitDValueMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> mulitALLDValueMap = new HashMap<String, BigDecimal>();// 后勤小店的分配比例
		BigDecimal distributionValue = BigDecimal.ZERO;
		this.mulitSaleHandler(todayDeptSaleList, mulitTodayDeptSaleListMap);
		this.mulitSaleHandler(historyDeptSaleList, mulitHistoryDeptSaleListMap);
		this.mulitGoalHandler(goalValueList, mulitGoalValueListMap);
		this.mulitStockHandler(deptStockList, mulitDeptStockListMap);
		this.mulitDistributionHandler(dValueList, mulitDValueMap, mulitALLDValueMap);
		List<ProgressCoreData> progressCoreDataList = new ArrayList<ProgressCoreData>();
		Map<String, String> deptToComMap = new HashMap<String, String>();
		this.deptInfoToComHandler(deptInfoList, deptToComMap);
		for (Map.Entry<String, String> entry : deptToComMap.entrySet()) {
			List<BaseReportGpExt> singleHistoryDeptSaleList = mulitHistoryDeptSaleListMap.get(entry.getKey());
			List<BaseReportGpExt> singleTodayDeptSaleList = mulitTodayDeptSaleListMap.get(entry.getKey());
			List<GoalValue> singleGoalValueList = mulitGoalValueListMap.get(entry.getKey());
			List<DeptStock> singleDeptStockList = mulitDeptStockListMap.get(entry.getKey());
			distributionValue = mulitDValueMap.get(entry.getKey());
			Map<String, BaseReportGpExt> todayDeptSaleMap = new HashMap<String, BaseReportGpExt>();
			Map<String, DeptInfo> deptMap = new HashMap<String, DeptInfo>();
			this.deptInfoHandler(deptInfoList, deptMap, entry.getValue());
			Map<String, IncomeConfig> incomeConfigMap = new HashMap<String, IncomeConfig>();
			String storeId = entry.getKey().split("_")[0];
			this.incomeCnfHandler(incomeCnfList, incomeConfigMap, storeId);
			Map<String, TurnOverDays> turnOverDaysMap = new HashMap<String, TurnOverDays>();
			this.turnOverDaysHandler(turnOverDaysList, turnOverDaysMap, storeId);
			this.todaySaleHandler(singleTodayDeptSaleList, todayDeptSaleMap);
			// 调用核心处理，封装所需要的数据
			ProgressCoreData progressCoreData = this.progressReportCore(singleGoalValueList, singleDeptStockList,
					beginYmd, endYmd, singleHistoryDeptSaleList, todayDeptSaleMap, incomeConfigMap, turnOverDaysMap,
					distributionValue, deptMap);
			progressCoreDataList.add(progressCoreData);
		}
		return this.mulitProgressCoreDataHandler(beginYmd, endYmd, progressCoreDataList, mulitALLDValueMap);
	}

	private ProgressCoreData mulitProgressCoreDataHandler(String beginYmd, String endYmd,
			List<ProgressCoreData> progressCoreDataList, Map<String, BigDecimal> mulitALLDValueMap) throws Exception {
		ProgressCoreData progressCoreData = new ProgressCoreData();
		Map<String, BigDecimal> allExcessProfitMap = new HashMap<String, BigDecimal>();// 后勤小店超额利润
		for (ProgressCoreData data : progressCoreDataList) {
			if (allExcessProfitMap.get(data.getStoreId()) != null) {
				allExcessProfitMap.put(data.getStoreId(),
						allExcessProfitMap.get(data.getStoreId()).add(data.getExcessProfit()));
			} else {
				allExcessProfitMap.put(data.getStoreId(), data.getExcessProfit());
			}
			progressCoreData.addActualSale(data.getActualSale());
			progressCoreData.addActualFrontGp(data.getActualFrontGp());
			progressCoreData.addActualAfterGp(data.getActualAfterGp());
			progressCoreData.addActualSaleCost(data.getActualSaleCost());
			progressCoreData.addActualTotalGp(data.getActualTotalGp());
			progressCoreData.addActualStock(data.getActualStock());
			progressCoreData.addSaleTotalGoal(data.getSaleTotalGoal());
			progressCoreData.addCurSaleGoal(data.getCurSaleGoal());
			progressCoreData.addSaleDiffVal(data.getSaleDiffVal());
			progressCoreData.addActualTotalCost(data.getActualTotalCost());
			progressCoreData.addCostTotalGoal(data.getCostTotalGoal());
			progressCoreData.addCurCostGoal(data.getCurCostGoal());
			progressCoreData.addCostDiffVal(data.getCostDiffVal());
			progressCoreData.addCurFrontGpGoal(data.getCurFrontGpGoal());
			progressCoreData.addFrontGpTotalGoal(data.getFrontGpTotalGoal());
			progressCoreData.addDcTotalGoal(data.getDcTotalGoal());
			progressCoreData.addAfterGpTotalGoal(data.getAfterGpTotalGoal());
			progressCoreData.addAttractTotalGoal(data.getAttractTotalGoal());
			progressCoreData.addCurAfterGpGoal(data.getCurAfterGpGoal());
			progressCoreData.addCurAttractGoal(data.getCurAttractGoal());
			progressCoreData.addGpTotalGoal(data.getGpTotalGoal());
			progressCoreData.addCurGpGoal(data.getCurGpGoal());
			progressCoreData.addGpDiffVal(data.getGpDiffVal());
			progressCoreData.addActualTotalProfit(data.getActualTotalProfit());
			progressCoreData.addProfitTotalGoal(data.getProfitTotalGoal());
			progressCoreData.addCurProfitGoal(data.getCurProfitGoal());
			progressCoreData.addProfitDiffVal(data.getProfitDiffVal());
			progressCoreData.addExcessProfit(data.getExcessProfit());
			progressCoreData.addShare(data.getShare());
			progressCoreData.getCurCostGoalObject().addDepreciation(data.getCurCostGoalObject().getDepreciation());
			progressCoreData.getCurCostGoalObject().addHydropower(data.getCurCostGoalObject().getHydropower());
			progressCoreData.getCurCostGoalObject().addLease(data.getCurCostGoalObject().getLease());
			progressCoreData.getCurCostGoalObject().addManpower(data.getCurCostGoalObject().getManpower());
			progressCoreData.getCurCostGoalObject().addOther(data.getCurCostGoalObject().getOther());
		}
		if (progressCoreData.getProfitTotalGoal().compareTo(BigDecimal.ZERO) == 0) {// 如果目标利润为0，预估分享金额为0
			progressCoreData.setShare(BigDecimal.ZERO);
		} else {
			for (Map.Entry<String, BigDecimal> entry : allExcessProfitMap.entrySet()) {
				progressCoreData.addShare(entry.getValue().multiply(mulitALLDValueMap.get(entry.getKey())).setScale(2,
						BigDecimal.ROUND_HALF_UP));// 累加各门店后勤小店的分享金额
			}
			progressCoreData.setShare(progressCoreData.getShare().compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO
					: progressCoreData.getShare());// 预估分享金额小于0的时候为0
		}
		int totalDays = DateUtil.getMonthTotalDay(beginYmd);
		int curDays = DateUtil.getBetweenDays(beginYmd, endYmd) + 1;
		progressCoreData.calculateRatio(totalDays, curDays);
		return progressCoreData;
	}

	/**
	 * 处理单个小店(包括后勤小店)目标数据
	 * 
	 * @param goalValueList
	 * @param totalSaleGoal
	 * @param totalFrontGpGoal
	 * @param totalAfterGpGoal
	 * @param totalDcGoal
	 * @param totalCostGoal
	 */
	protected GoalSummary goalHandler(List<GoalValue> goalValueList) {
		BigDecimal saleTotalGoal = BigDecimal.ZERO;
		BigDecimal frontGpTotalGoal = BigDecimal.ZERO;
		BigDecimal afterGpTotalGoal = BigDecimal.ZERO;
		BigDecimal dcTotalGoal = BigDecimal.ZERO;
		BigDecimal costTotalGoal = BigDecimal.ZERO;
		BigDecimal attractTotalGoal = BigDecimal.ZERO;
		CostVal costVal = new CostVal();
		if (goalValueList != null && goalValueList.size() > 0) {
			for (GoalValue goalValue : goalValueList) {
				if ("销售".equals(goalValue.getSubject())) {
					saleTotalGoal = goalValue.getSubValues();
				}
				if ("毛利额".equals(goalValue.getSubject())) {
					frontGpTotalGoal = goalValue.getSubValues();
				}
				if ("后台".equals(goalValue.getSubject())) {
					afterGpTotalGoal = goalValue.getSubValues();
				}
				if ("招商收入".equals(goalValue.getSubject())) {
					attractTotalGoal = goalValue.getSubValues();
				}
				if ("DC成本".equals(goalValue.getSubject())) {
					dcTotalGoal = goalValue.getSubValues();
				}
				if ("其它费用".equals(goalValue.getSubject())) {
					costVal.setOther(goalValue.getSubValues());
					costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
				}
				if ("折旧费用".equals(goalValue.getSubject())) {
					costVal.setDepreciation(goalValue.getSubValues());
					costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
				}
				if ("租赁费用".equals(goalValue.getSubject())) {
					costVal.setLease(goalValue.getSubValues());
					costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
				}
				if ("销售-水电费用".equals(goalValue.getSubject())) {
					costVal.setHydropower(goalValue.getSubValues());
					costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
				}
				if ("销售-人力成本".equals(goalValue.getSubject())) {
					costVal.setManpower(goalValue.getSubValues());
					costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
				}
			}
		}
		return new GoalSummary(costVal, saleTotalGoal, frontGpTotalGoal, afterGpTotalGoal, attractTotalGoal,
				dcTotalGoal, costTotalGoal);
	}

	/**
	 * 处理多个小店目标数据
	 * 
	 * @param goalValueList
	 * @param totalSaleGoal
	 * @param totalFrontGpGoal
	 * @param totalAfterGpGoal
	 * @param totalDcGoal
	 * @param totalCostGoal
	 */
	protected GoalSummary mulitGoalHandler(List<GoalValue> goalValueList) {
		BigDecimal saleTotalGoal = BigDecimal.ZERO;
		BigDecimal frontGpTotalGoal = BigDecimal.ZERO;
		BigDecimal afterGpTotalGoal = BigDecimal.ZERO;
		BigDecimal dcTotalGoal = BigDecimal.ZERO;
		BigDecimal costTotalGoal = BigDecimal.ZERO;
		BigDecimal attractTotalGoal = BigDecimal.ZERO;
		CostVal costVal = new CostVal();
		if (goalValueList != null && goalValueList.size() > 0) {
			for (GoalValue goalValue : goalValueList) {
				if (!"ALL".equals(goalValue.getComId())) {// 累计不包括后勤小店的目标
					if ("销售".equals(goalValue.getSubject())) {
						saleTotalGoal = saleTotalGoal.add(goalValue.getSubValues());
					}
					if ("毛利额".equals(goalValue.getSubject())) {
						frontGpTotalGoal = frontGpTotalGoal.add(goalValue.getSubValues());
					}
					if ("后台".equals(goalValue.getSubject())) {
						afterGpTotalGoal = afterGpTotalGoal.add(goalValue.getSubValues());
					}
					if ("招商收入".equals(goalValue.getSubject())) {
						attractTotalGoal = attractTotalGoal.add(goalValue.getSubValues());
					}
					if ("DC成本".equals(goalValue.getSubject())) {
						dcTotalGoal = dcTotalGoal.add(goalValue.getSubValues());
					}
					if ("其它费用".equals(goalValue.getSubject())) {
						costVal.addOther(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
					if ("折旧费用".equals(goalValue.getSubject())) {
						costVal.addDepreciation(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
					if ("租赁费用".equals(goalValue.getSubject())) {
						costVal.addLease(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
					if ("销售-水电费用".equals(goalValue.getSubject())) {
						costVal.addHydropower(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
					if ("销售-人力成本".equals(goalValue.getSubject())) {
						costVal.addManpower(goalValue.getSubValues());
						costTotalGoal = costTotalGoal.add(goalValue.getSubValues());
					}
				}
			}
		}
		return new GoalSummary(costVal, saleTotalGoal, frontGpTotalGoal, afterGpTotalGoal, attractTotalGoal,
				dcTotalGoal, costTotalGoal);
	}

	/**
	 * 进度报表核心处理
	 * 
	 * @param goalValueList
	 * @param beginYmd
	 * @param endYmd
	 * @param historyDeptSaleList
	 * @param todayDeptSaleMap
	 * @param incomeConfigMap
	 * @return
	 */
	private ProgressCoreData progressReportCore(List<GoalValue> goalValueList, List<DeptStock> deptStockList,
			String beginYmd, String endYmd, List<BaseReportGpExt> historyDeptSaleList,
			Map<String, BaseReportGpExt> todayDeptSaleMap, Map<String, IncomeConfig> incomeConfigMap,
			Map<String, TurnOverDays> turnOverDaysMap, BigDecimal distributionValue, Map<String, DeptInfo> deptMap)
			throws Exception {
		ProgressCoreData progressCoreData = new ProgressCoreData();
		GoalSummary goalSummary = this.goalHandler(goalValueList);
		if (goalSummary.getSaleTotalGoal() != null) {
			progressCoreData.setSaleTotalGoal(goalSummary.getSaleTotalGoal());
		}
		if (goalSummary.getFrontGpTotalGoal() != null) {
			progressCoreData.setFrontGpTotalGoal(goalSummary.getFrontGpTotalGoal());
		}
		if (goalSummary.getAfterGpTotalGoal() != null) {
			progressCoreData.setAfterGpTotalGoal(goalSummary.getAfterGpTotalGoal());
		}
		if (goalSummary.getDcTotalGoal() != null) {
			progressCoreData.setDcTotalGoal(goalSummary.getDcTotalGoal());
		}
		if (goalSummary.getCostTotalGoal() != null) {
			progressCoreData.setCostTotalGoal(goalSummary.getCostTotalGoal());
		}
		if (goalSummary.getAttractTotalGoal() != null) {
			progressCoreData.setAttractTotalGoal(goalSummary.getAttractTotalGoal());
		}
		int totalDays = DateUtil.getMonthTotalDay(beginYmd);
		int curDays = DateUtil.getBetweenDays(beginYmd, endYmd) + 1;
		// 各项费用当前累计目标
		progressCoreData.setCurCostGoalObject(goalSummary.getCostValObject().getCurCost(totalDays, curDays));
		progressCoreData.setCurSaleGoal(
				OperationUtil.divideHandler(progressCoreData.getSaleTotalGoal(), new BigDecimal(totalDays))
						.multiply(new BigDecimal(curDays)));// 当月累计销售目标
		progressCoreData.setCurCostGoal(
				OperationUtil.divideHandler(progressCoreData.getCostTotalGoal(), new BigDecimal(totalDays))
						.multiply(new BigDecimal(curDays)));// 当月累计费用目标
		progressCoreData.setCurAttractGoal(
				OperationUtil.divideHandler(progressCoreData.getAttractTotalGoal(), new BigDecimal(totalDays))
						.multiply(new BigDecimal(curDays)));// 当月累计招商目标
		progressCoreData.setCurFrontGpGoal(
				OperationUtil.divideHandler(progressCoreData.getFrontGpTotalGoal(), new BigDecimal(totalDays))
						.multiply(new BigDecimal(curDays)));// 当前累计目标前台毛利
		progressCoreData.setCurAfterGpGoal(
				OperationUtil.divideHandler(progressCoreData.getAfterGpTotalGoal(), new BigDecimal(totalDays))
						.multiply(new BigDecimal(curDays)));// 当前累计目标后台收入
		// 目标总毛利=目标前台毛利+目标后台收入-DC成本
		progressCoreData.setGpTotalGoal(progressCoreData.getFrontGpTotalGoal()
				.add(progressCoreData.getAfterGpTotalGoal()).subtract(progressCoreData.getDcTotalGoal()));
		progressCoreData
				.setCurGpGoal(OperationUtil.divideHandler(progressCoreData.getGpTotalGoal(), new BigDecimal(totalDays))
						.multiply(new BigDecimal(curDays)));
		Map<String, BaseReportGpExt> historyDeptSaleMap = new HashMap<String, BaseReportGpExt>();
		this.historySaleHandler(historyDeptSaleList, historyDeptSaleMap);
		for (Map.Entry<String, DeptInfo> entry : deptMap.entrySet()) {
			BaseReportGpExt historyDeptSale = historyDeptSaleMap.get(entry.getKey());
			// 设置门店编码（多个小店统计的时候使用）
			progressCoreData.setStoreId(entry.getValue().getStoreId());
			// 设置小店编码（多个小店统计的时候使用）
			progressCoreData.setComId(entry.getValue().getComId());
			BigDecimal actualSaleItem = BigDecimal.ZERO;
			BigDecimal actualFrontGpItem = BigDecimal.ZERO;
			BigDecimal actualAfterGpItem = BigDecimal.ZERO;
			if (historyDeptSale != null) {
				actualSaleItem = historyDeptSale.getValue();
				// 实际前台毛利=销售金额（未税）-销售成本（未税）-券金额（未税）+损耗（未税）-成本调整（未税）+销售补差（未税）+未税未券扫描毛利
				actualFrontGpItem = historyDeptSale.getGpValue();
				actualAfterGpItem = historyDeptSale.getValue();// 后台毛利先等于销售，后面再乘以收入率
				progressCoreData.addActualSaleCost(historyDeptSale.getSaleCost());// 销售成本只有历史销售才有，当日销售没有
			}
			if (todayDeptSaleMap.get(entry.getKey()) != null) {
				actualSaleItem = actualSaleItem.add(todayDeptSaleMap.get(entry.getKey()).getValue());
				actualFrontGpItem = actualFrontGpItem.add(todayDeptSaleMap.get(entry.getKey()).getGpValue());
				actualAfterGpItem = actualAfterGpItem.add(todayDeptSaleMap.get(entry.getKey()).getValue());
			}
			// 实际后台收入=实际销售额*各大类固定后台收入率【用各大类的销售额*各大类的后台收入率再求和】
			BigDecimal inValues = BigDecimal.ZERO;// 默认后台收入率为0
			if (incomeConfigMap.get(entry.getKey()) != null) {
				inValues = incomeConfigMap.get(entry.getKey()).getInValues();
			}
			// 后台毛利=销售*后台收入率
			actualAfterGpItem = actualAfterGpItem.multiply(inValues);
			progressCoreData.addActualSale(actualSaleItem);
			progressCoreData.addActualFrontGp(actualFrontGpItem);
			progressCoreData.addActualAfterGp(actualAfterGpItem);
			// 实际总毛利=实际前台毛利+实际后台收入-DC收入率*销售金额
			BigDecimal dcValues = BigDecimal.ZERO;// 默认DC收入率为0
			if (incomeConfigMap.get(entry.getKey()) != null) {
				dcValues = incomeConfigMap.get(entry.getKey()).getDcValues();
			}
			progressCoreData.addActualTotalGp(
					actualFrontGpItem.add(actualAfterGpItem).subtract(actualSaleItem.multiply(dcValues)));
		}
		// 日均销售成本
		BigDecimal daySaleCost = OperationUtil.divideHandler(progressCoreData.getActualSaleCost(),
				new BigDecimal(curDays));
		// 库存占用资金：[剩余库存金额-（日均销售成本*标准周转天数）]*0.4%
		if (deptStockList != null && deptStockList.size() > 0) {
			for (DeptStock deptStock : deptStockList) {
				BigDecimal turnOverDays = BigDecimal.ZERO;// 标准周转天数
				if (turnOverDaysMap.get(deptStock.getDeptId()) != null) {
					turnOverDays = new BigDecimal(turnOverDaysMap.get(deptStock.getDeptId()).getDays());
				}
				progressCoreData.addActualStock(deptStock.getStockMoney().subtract(daySaleCost.multiply(turnOverDays))
						.multiply(new BigDecimal(0.004)));
			}
		}
		progressCoreData.setActualStock(OperationUtil.nonNegativeHandler(progressCoreData.getActualStock()));
		progressCoreData.operation(distributionValue);
		return progressCoreData.calculateRatio(totalDays, curDays);
	}

	/**
	 * 处理单个小店历史销售数据
	 * 
	 * @author wells
	 * @param historyDeptSaleList
	 * @param historyDeptSaleMap
	 * @time 2018年12月26日
	 */
	private void historySaleHandler(List<BaseReportGpExt> historyDeptSaleList,
			Map<String, BaseReportGpExt> historyDeptSaleListMap) {
		if (historyDeptSaleList != null && historyDeptSaleList.size() > 0) {
			historyDeptSaleList.stream().forEach(baseReportGpExt -> {
				BaseReportGpExt oldBaseReportGpExt = historyDeptSaleListMap.get(baseReportGpExt.getOrgId());
				if (oldBaseReportGpExt == null) {
					historyDeptSaleListMap.put(baseReportGpExt.getOrgId(), baseReportGpExt);
				} else {
					oldBaseReportGpExt.setSaleCost(oldBaseReportGpExt.getSaleCost().add(baseReportGpExt.getSaleCost()));
					oldBaseReportGpExt.setGpValue(oldBaseReportGpExt.getGpValue().add(baseReportGpExt.getGpValue()));
					oldBaseReportGpExt.setValue(oldBaseReportGpExt.getValue().add(baseReportGpExt.getValue()));
				}
			});
		}
	}

	/**
	 * 处理单个小店当天销售数据
	 * 
	 * @param todayDeptSaleList
	 * @param todayDeptSaleMap
	 */
	private void todaySaleHandler(List<BaseReportGpExt> todayDeptSaleList,
			Map<String, BaseReportGpExt> todayDeptSaleMap) {
		if (todayDeptSaleList != null && todayDeptSaleList.size() > 0) {
			todayDeptSaleList.stream().forEach(baseReportGpExt -> {
				BaseReportGpExt oldBaseReportGpExt = todayDeptSaleMap.get(baseReportGpExt.getOrgId());
				if (oldBaseReportGpExt == null) {
					todayDeptSaleMap.put(baseReportGpExt.getOrgId(), baseReportGpExt);
				} else {
					oldBaseReportGpExt.setGpValue((oldBaseReportGpExt.getGpValue() == null ? BigDecimal.ZERO
							: oldBaseReportGpExt.getGpValue())
									.add(baseReportGpExt.getGpValue() == null ? BigDecimal.ZERO
											: baseReportGpExt.getGpValue()));
					oldBaseReportGpExt.setValue(
							(oldBaseReportGpExt.getValue() == null ? BigDecimal.ZERO : oldBaseReportGpExt.getValue())
									.add(baseReportGpExt.getValue() == null ? BigDecimal.ZERO
											: baseReportGpExt.getValue()));
				}
			});
		}
	}

	/**
	 * 处理多个小店销售数据<br>
	 * 支持处理当天及历史销售数据
	 * 
	 * @param todayDeptSaleList
	 * @param todayDeptSaleMap
	 */
	private void mulitSaleHandler(List<BaseReportGpExt> deptSaleList,
			Map<String, List<BaseReportGpExt>> mulitDeptSaleListMap) {
		if (deptSaleList != null && deptSaleList.size() > 0) {
			deptSaleList.stream().forEach(baseReportGpExt -> {
				List<BaseReportGpExt> oldBaseReportGpExtList = mulitDeptSaleListMap
						.get(baseReportGpExt.getStoreId() + "_" + baseReportGpExt.getComId());
				if (oldBaseReportGpExtList == null) {
					List<BaseReportGpExt> newBaseReportGpExtList = new ArrayList<BaseReportGpExt>();
					newBaseReportGpExtList.add(baseReportGpExt);
					mulitDeptSaleListMap.put(baseReportGpExt.getStoreId() + "_" + baseReportGpExt.getComId(),
							newBaseReportGpExtList);
				} else {
					oldBaseReportGpExtList.add(baseReportGpExt);
				}
			});
		}
	}

	/**
	 * 处理多个小店的目标数据
	 * 
	 * @param goalValueList
	 * @param mulitGoalValueMap
	 * @author wells
	 * @date 2019年1月8日
	 */
	private void mulitGoalHandler(List<GoalValue> goalValueList, Map<String, List<GoalValue>> mulitGoalValueListMap) {
		if (goalValueList != null && goalValueList.size() > 0) {
			goalValueList.stream().forEach(goalValue -> {
				List<GoalValue> oldGoalValueList = mulitGoalValueListMap
						.get(goalValue.getStoreId() + "_" + goalValue.getComId());
				if (oldGoalValueList == null) {
					List<GoalValue> newGoalValueList = new ArrayList<GoalValue>();
					newGoalValueList.add(goalValue);
					mulitGoalValueListMap.put(goalValue.getStoreId() + "_" + goalValue.getComId(), newGoalValueList);
				} else {
					oldGoalValueList.add(goalValue);
				}
			});
		}
	}

	/**
	 * 处理多个小店的库存数据
	 * 
	 * @param goalValueList
	 * @param mulitGoalValueMap
	 * @author wells
	 * @date 2019年1月8日
	 */
	private void mulitStockHandler(List<DeptStock> deptStockList, Map<String, List<DeptStock>> mulitdeptStockListMap) {
		if (deptStockList != null && deptStockList.size() > 0) {
			deptStockList.stream().forEach(deptStock -> {
				List<DeptStock> oldDeptStockList = mulitdeptStockListMap
						.get(deptStock.getStoreId() + "_" + deptStock.getComId());
				if (oldDeptStockList == null) {
					List<DeptStock> newDeptStockList = new ArrayList<DeptStock>();
					newDeptStockList.add(deptStock);
					mulitdeptStockListMap.put(deptStock.getStoreId() + "_" + deptStock.getComId(), newDeptStockList);
				} else {
					oldDeptStockList.add(deptStock);
				}
			});
		}
	}

	/**
	 * 处理多个小店的分配比例数据
	 * 
	 * @param dValueList
	 * @param mulitDValueMap
	 * @author wells
	 * @date 2019年1月8日
	 */
	private void mulitDistributionHandler(List<DistributionValues> dValueList, Map<String, BigDecimal> mulitDValueMap,
			Map<String, BigDecimal> mulitALLDValueMap) {
		if (dValueList != null && dValueList.size() > 0) {
			dValueList.stream().forEach(dValues -> {
				if ("ALL".equals(dValues.getComId())) {
					mulitALLDValueMap.put(dValues.getStoreId(), dValues.getValue());
				}
				mulitDValueMap.put(dValues.getStoreId() + "_" + dValues.getComId(), dValues.getValue());
			});
		}
	}

	/**
	 * 处理大类信息
	 * 
	 * @param deptInfoList
	 * @param deptMap
	 * @author wells
	 * @date 2019年3月1日
	 */
	private void deptInfoHandler(List<DeptInfo> deptInfoList, Map<String, DeptInfo> deptMap) {
		if (deptInfoList != null && deptInfoList.size() > 0) {
			deptInfoList.stream().forEach(deptInfo -> {
				if (!deptMap.containsKey(deptInfo.getDeptId())) {
					deptMap.put(deptInfo.getDeptId(), deptInfo);
				}
			});
		}
	}

	/**
	 * 处理大类信息
	 * 
	 * @param deptInfoList
	 * @param deptMap
	 * @author wells
	 * @date 2019年3月1日
	 */
	private void deptInfoHandler(List<DeptInfo> deptInfoList, Map<String, DeptInfo> deptMap, String comId) {
		if (deptInfoList != null && deptInfoList.size() > 0) {
			deptInfoList.stream().forEach(deptInfo -> {
				if (deptInfo.getComId().equals(comId) && !deptMap.containsKey(deptInfo.getDeptId())) {
					deptMap.put(deptInfo.getDeptId(), deptInfo);
				}
			});
		}
	}

	/**
	 * 处理对应门店的大类DC收入率及后台收入率
	 * 
	 * @param incomeCnfList
	 * @param incomeConfigMap
	 * @param storeId
	 * @author wells
	 * @date 2019年4月26日
	 */
	private void incomeCnfHandler(List<IncomeConfig> incomeCnfList, Map<String, IncomeConfig> incomeConfigMap,
			String storeId) {
		if (incomeCnfList != null && incomeCnfList.size() > 0) {
			incomeCnfList.stream().forEach(incomeCnf -> {
				if (incomeCnf.getStoreId().equals(storeId) && !incomeConfigMap.containsKey(incomeCnf.getDeptId())) {
					incomeConfigMap.put(incomeCnf.getDeptId(), incomeCnf);
				}
			});
		}
	}

	/**
	 * 处理对应门店标准周转天数
	 * 
	 * @param turnOverDaysList
	 * @param turnOverDaysMap
	 * @param storeId
	 * @author wells
	 * @date 2019年4月26日
	 */
	private void turnOverDaysHandler(List<TurnOverDays> turnOverDaysList, Map<String, TurnOverDays> turnOverDaysMap,
			String storeId) {
		if (turnOverDaysList != null && turnOverDaysList.size() > 0) {
			turnOverDaysList.stream().forEach(turnOverDays -> {
				if (turnOverDays.getStoreId().equals(storeId)
						&& !turnOverDaysMap.containsKey(turnOverDays.getDeptId())) {
					turnOverDaysMap.put(turnOverDays.getDeptId(), turnOverDays);
				}
			});
		}
	}

	/**
	 * 从大类信息抽取小店
	 * 
	 * @param deptInfoList
	 * @param deptStrMap
	 * @author wells
	 * @date 2019年3月1日
	 */
	private void deptInfoToComHandler(List<DeptInfo> deptInfoList, Map<String, String> deptToComMap) {
		if (deptInfoList != null && deptInfoList.size() > 0) {
			deptInfoList.stream().forEach(deptInfo -> {
				String key = deptInfo.getStoreId() + "_" + deptInfo.getComId();
				if (!deptToComMap.containsKey(key)) {
					deptToComMap.put(key, deptInfo.getComId());
				}
			});
		}
	}
}

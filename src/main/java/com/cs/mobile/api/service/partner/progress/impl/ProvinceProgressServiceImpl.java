package com.cs.mobile.api.service.partner.progress.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.common.CommonDao;
import com.cs.mobile.api.dao.common.DeptInfoDao;
import com.cs.mobile.api.dao.partner.assess.ProvinceAssessDao;
import com.cs.mobile.api.dao.partner.goal.GoalDao;
import com.cs.mobile.api.dao.partner.progress.ProvinceProgressDao;
import com.cs.mobile.api.model.common.DeptInfo;
import com.cs.mobile.api.model.goal.GoalSummary;
import com.cs.mobile.api.model.goal.GoalValue;
import com.cs.mobile.api.model.partner.assess.AssessResult;
import com.cs.mobile.api.model.partner.battle.Accounting;
import com.cs.mobile.api.model.partner.battle.BaseReportGpExt;
import com.cs.mobile.api.model.partner.progress.AccountingItemResult;
import com.cs.mobile.api.model.partner.progress.AssessItemResult;
import com.cs.mobile.api.model.partner.progress.DeptStock;
import com.cs.mobile.api.model.partner.progress.DistributionValues;
import com.cs.mobile.api.model.partner.progress.IncomeConfig;
import com.cs.mobile.api.model.partner.progress.ProgressCoreData;
import com.cs.mobile.api.model.partner.progress.ProgressReport;
import com.cs.mobile.api.model.partner.progress.ProgressReportResult;
import com.cs.mobile.api.model.partner.progress.ShareDetail;
import com.cs.mobile.api.model.partner.progress.TurnOverDays;
import com.cs.mobile.api.service.partner.progress.ProgressAbstractService;
import com.cs.mobile.api.service.partner.progress.ProvinceProgressService;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.OperationUtil;

/**
 * 全司下所有省份的进度报表服务
 * 
 * @author wells
 * @time 2018年12月13日
 */

@Service
public class ProvinceProgressServiceImpl extends ProgressAbstractService implements ProvinceProgressService {
	@Autowired
	CommonDao commonDao;
	@Autowired
	GoalDao goalDao;
	@Autowired
	ProvinceProgressDao provinceProgressDao;
	@Autowired
	ProvinceAssessDao provinceAssessDao;
	@Autowired
	DeptInfoDao deptInfoDao;

	/**
	 * 获取进度报表数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public ProgressReportResult getProgressReport(String beginYmd, String endYmd) throws Exception {
		String ym = beginYmd.substring(0, 7);
		List<DistributionValues> dValueList = provinceProgressDao.getComDistributionCnf();// 小店分配比例
		List<BaseReportGpExt> todayDeptSaleList = null;
		if (DateUtil.getBetweenDays(endYmd, DateUtil.getDate("yyyy-MM-dd")) == 0) {// 只有结束时间包含当天才需要累计当天的销售
			todayDeptSaleList = provinceProgressDao.getTodayDeptSale();// 当日小店所有大类实时销售
		}
		List<BaseReportGpExt> historyDeptSaleList = provinceProgressDao.getHistoryDeptSale(beginYmd, endYmd);// 小店下所有大类历史销售
		List<GoalValue> goalValueList = goalDao.getProvinceGPGoal(ym);// 整月小店各项目标
		List<IncomeConfig> incomeCnfList = commonDao.getAllIncomeCnf(ym);// 获取后台收入率列表
		List<DeptStock> deptStockList = provinceProgressDao.getDeptStock(DateUtil.getCurMonthYesterday(endYmd));// 大类库存
		List<TurnOverDays> turnOverDaysList = provinceProgressDao.getDeptTurnoverDays(ym);// 标准周转天数
		List<DeptInfo> deptInfoList = deptInfoDao.getAllDeptInfo();
		ProgressCoreData data = this.getMulitProgressCoreData(beginYmd, endYmd, todayDeptSaleList, historyDeptSaleList,
				goalValueList, incomeCnfList, deptStockList, turnOverDaysList, dValueList, deptInfoList);
		// 超时间进度/差时间进度=（当月截止当前时间实际销售额-当月截止当前时间目标销售额）/当月截止当前时间目标销售额
		// 销售
		ProgressReport sale = new ProgressReport.Builder().typeName("销售")
				.goalCountVal(OperationUtil.divideHandler(data.getSaleTotalGoal(), new BigDecimal(10000)))
				.goalVal(OperationUtil.divideHandler(data.getCurSaleGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualSale(), new BigDecimal(10000)))
				.actualRatio(data.getActualSaleRatio()).goalRatio(data.getGoalSaleRatio())
				.diffVal(OperationUtil.divideHandler(data.getSaleDiffVal(), new BigDecimal(10000)))
				.diffRatio(data.getDiffSaleRatio()).unit("万").build();
		// 费用
		ProgressReport cost = new ProgressReport.Builder().typeName("费用")
				.goalCountVal(OperationUtil.divideHandler(data.getCostTotalGoal(), new BigDecimal(10000)))
				.goalVal(OperationUtil.divideHandler(data.getCurCostGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualTotalCost(), new BigDecimal(10000)))
				.actualRatio(data.getActualCostRatio()).goalRatio(data.getGoalCostRatio())
				.diffVal(OperationUtil.divideHandler(data.getCostDiffVal(), new BigDecimal(10000)))
				.diffRatio(data.getDiffCostRatio()).unit("万").build();
		// 毛利
		ProgressReport gp = new ProgressReport.Builder().typeName("毛利")
				.goalCountVal(OperationUtil.divideHandler(data.getGpTotalGoal(), new BigDecimal(10000)))
				.goalVal(OperationUtil.divideHandler(data.getCurGpGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualTotalGp(), new BigDecimal(10000)))
				.actualRatio(data.getActualGpRatio()).goalRatio(data.getGoalGpRatio())
				.diffVal(OperationUtil.divideHandler(data.getGpDiffVal(), new BigDecimal(10000)))
				.diffRatio(data.getDiffGpRatio()).unit("万").build();
		// 利润
		ProgressReport profit = new ProgressReport.Builder().typeName("利润")
				.goalCountVal(OperationUtil.divideHandler(data.getProfitTotalGoal(), new BigDecimal(10000)))
				.goalVal(OperationUtil.divideHandler(data.getCurProfitGoal(), new BigDecimal(10000)))
				.actualVal(OperationUtil.divideHandler(data.getActualTotalProfit(), new BigDecimal(10000)))
				.actualRatio(data.getActualProfitRatio()).goalRatio(data.getGoalProfitRatio())
				.diffVal(OperationUtil.divideHandler(data.getProfitDiffVal(), new BigDecimal(10000)))
				.diffRatio(data.getDiffProfitRatio()).unit("万").build();
		return new ProgressReportResult(gp, profit, cost, sale, data.getShare(), "元");
	}

	/**
	 * 获取进度报表数据详情
	 * 
	 * @param groupId
	 * @param storeId
	 * @param comId
	 * @param beginYmd
	 * @param endYmd
	 * @return
	 * @throws Exception
	 */
	public ShareDetail getProgressReportDetail(String beginYmd, String endYmd) throws Exception {
		String ym = beginYmd.substring(0, 7);
		List<DistributionValues> dValueList = provinceProgressDao.getComDistributionCnf();// 小店分配比例
		List<BaseReportGpExt> todayDeptSaleList = null;
		if (DateUtil.getBetweenDays(endYmd, DateUtil.getDate("yyyy-MM-dd")) == 0) {// 只有结束时间包含当天才需要累计当天的销售
			todayDeptSaleList = provinceProgressDao.getTodayDeptSale();// 当日小店所有大类实时销售
		}
		List<BaseReportGpExt> historyDeptSaleList = provinceProgressDao.getHistoryDeptSale(beginYmd, endYmd);// 小店下所有大类历史销售
		List<GoalValue> goalValueList = goalDao.getProvinceGPGoal(ym);// 整月小店各项目标
		List<IncomeConfig> incomeCnfList = commonDao.getAllIncomeCnf(ym);// 获取后台收入率列表
		List<DeptStock> deptStockList = provinceProgressDao.getDeptStock(DateUtil.getCurMonthYesterday(endYmd));// 大类库存
		List<TurnOverDays> turnOverDaysList = provinceProgressDao.getDeptTurnoverDays(ym);// 标准周转天数
		List<DeptInfo> deptInfoList = deptInfoDao.getAllDeptInfo();
		ProgressCoreData data = this.getMulitProgressCoreData(beginYmd, endYmd, todayDeptSaleList, historyDeptSaleList,
				goalValueList, incomeCnfList, deptStockList, turnOverDaysList, dValueList, deptInfoList);
		ShareDetail shareDetail = new ShareDetail();
		shareDetail.setAfterGp(OperationUtil.divideHandler(data.getActualAfterGp(), new BigDecimal(10000)));
		shareDetail.setAttract(OperationUtil.divideHandler(data.getActualAttract(), new BigDecimal(10000)));
		shareDetail.setCheckProfit(OperationUtil.divideHandler(data.getActualTotalGp().subtract(data.getCurCostGoal()),
				new BigDecimal(10000)));// 实际利润=总毛利-总费用
		shareDetail.setCostCount(OperationUtil.divideHandler(data.getCurCostGoal(), new BigDecimal(10000)));
		shareDetail.setCostList(data.getCurCostGoalObject().unitConvert(new BigDecimal(10000)));
		shareDetail.setCountGp(OperationUtil.divideHandler(data.getActualTotalGp(), new BigDecimal(10000)));
		shareDetail.setExcessProfit(OperationUtil.divideHandler(data.getExcessProfit(), new BigDecimal(10000)));
		shareDetail.setFrontGp(OperationUtil.divideHandler(data.getActualFrontGp(), new BigDecimal(10000)));
		// 实际总收入=实际总毛利+实际招商收入
		shareDetail.setIncomeCount(OperationUtil.divideHandler(data.getActualTotalGp().add(data.getActualAttract()),
				new BigDecimal(10000)));
		shareDetail.setSale(OperationUtil.divideHandler(data.getActualSale(), new BigDecimal(10000)));
		shareDetail.setStock(OperationUtil.divideHandler(data.getActualStock(), new BigDecimal(10000)));
		shareDetail.setShareUnit("元");
		shareDetail.setShareval(data.getShare());
		shareDetail.setUnit("万");
		return shareDetail;
	}

	public List<Accounting> getAccountingList() throws Exception {
		List<AssessResult> assessResultList = provinceAssessDao.getLastYearResult();
		Map<String, AssessItemResult> assessItemResultMap = new HashMap<String, AssessItemResult>();
		this.assessHandler(assessItemResultMap, assessResultList);
		List<Accounting> result = new ArrayList<Accounting>();
		assessItemResultMap.forEach((ym, assessItemResult) -> {
			Accounting accounting = new Accounting();
			accounting.setSaleval(OperationUtil.divideHandler(assessItemResult.getSale(), new BigDecimal(10000)));
			accounting.setAcostval(OperationUtil.divideHandler(assessItemResult.getTotalCost(), new BigDecimal(10000)));
			accounting.setShareval(OperationUtil.divideHandler(assessItemResult.getShareval(), new BigDecimal(10000)));
			accounting.setAttractval(OperationUtil.divideHandler(assessItemResult.getAttract(), new BigDecimal(10000)));
			// 实际利润=总毛利-总费用 实际总毛利=实际前台毛利+实际后台收入-DC成本
			accounting
					.setAprofitval(
							OperationUtil.divideHandler(
									assessItemResult.getFrontGp()
											.add(assessItemResult.getAfterGp().subtract(assessItemResult.getDcCost())
													.subtract(assessItemResult.getTotalCost())),
									new BigDecimal(10000)));
			accounting.setProfitrate(accounting.getAprofitval()
					.divide(accounting.getSaleval(), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));
			accounting.setGprate(assessItemResult.getFrontGp().divide(accounting.getSaleval()));
			accounting.setYmonth(ym);
			accounting.setUnit("万");
			accounting.setShareUnit("元");
			result.add(accounting);
		});
		return result;
	}

	public AccountingItemResult getAccountingItem(String beginYmd, String endYmd) throws Exception {
		String ym = beginYmd.substring(0, 7);
		List<GoalValue> goalValueList = goalDao.getProvinceGPGoal(ym);// 整月小店各项目标
		// 如果是当月，那么需要计算
		if (DateUtil.isCurMonth(beginYmd)) {
			List<BaseReportGpExt> todayDeptSaleList = null;
			if (DateUtil.getBetweenDays(endYmd, DateUtil.getDate("yyyy-MM-dd")) == 0) {// 只有结束时间包含当天才需要累计当天的销售
				todayDeptSaleList = provinceProgressDao.getTodayDeptSale();// 当日小店所有大类实时销售
			}
			List<BaseReportGpExt> historyDeptSaleList = provinceProgressDao.getHistoryDeptSale(beginYmd, endYmd);// 小店下所有大类历史销售
			List<IncomeConfig> incomeCnfList = commonDao.getAllIncomeCnf(ym);// 获取后台收入率列表
			List<DistributionValues> dValueList = provinceProgressDao.getComDistributionCnf();// 小店分配比例
			List<DeptStock> deptStockList = provinceProgressDao.getDeptStock(DateUtil.getCurMonthYesterday(endYmd));// 大类库存
			List<TurnOverDays> turnOverDaysList = provinceProgressDao.getDeptTurnoverDays(ym);// 标准周转天数
			List<DeptInfo> deptInfoList = deptInfoDao.getAllDeptInfo();
			ProgressCoreData data = this.getMulitProgressCoreData(beginYmd, endYmd, todayDeptSaleList,
					historyDeptSaleList, goalValueList, incomeCnfList, deptStockList, turnOverDaysList, dValueList,
					deptInfoList);
			return this.loadCurMonthAccountingItem(data);
		} else {// 如果是历史月，那么从考核表取数据
			GoalSummary goalSummary = this.mulitGoalHandler(goalValueList);
			List<AssessResult> assessResultList = provinceAssessDao.getResultByYm(ym);
			AssessItemResult assessItemResult = new AssessItemResult();
			for (AssessResult assessResult : assessResultList) {
				this.loadAssessItemResult(assessResult, assessItemResult);
			}
			return this.loadHistoryAccountingItem(goalSummary, assessItemResult);
		}
	}
}

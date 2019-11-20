package com.cs.mobile.api.service.partner.runindex.impl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.partner.goal.GoalDao;
import com.cs.mobile.api.dao.partner.runindex.RunIndexDao;
import com.cs.mobile.api.model.goal.Goal;
import com.cs.mobile.api.model.partner.runindex.RunBaseReport;
import com.cs.mobile.api.model.partner.runindex.RunIndexResult;
import com.cs.mobile.api.model.partner.runindex.RunWorkEffect;
import com.cs.mobile.api.model.partner.runindex.WorkLevelConfig;
import com.cs.mobile.api.model.user.UserInfo;
import com.cs.mobile.api.service.partner.runindex.RunIndexService;
import com.cs.mobile.common.utils.DateUtil;
import com.cs.mobile.common.utils.OperationUtil;
import com.google.common.collect.Lists;

@Service
public class RunIndexServiceImpl implements RunIndexService {

	@Autowired
	RunIndexDao runIndexDao;
	@Autowired
	GoalDao goalDao;

	@Override
	public RunIndexResult getRunReportResult(UserInfo userInfo) throws Exception {
		RunIndexResult runReportResult = runIndexDao.getStoreScale(userInfo.getPartnerUserInfo().getStoreId());
		// 上个月大店销售金额
		BigDecimal saleTotal = runIndexDao.getLastMonthStoreSaleTotal(userInfo.getPartnerUserInfo().getStoreId());
		// 上个月大店下所有人员的总时长
		BigDecimal workDays = runIndexDao.getLastMonthStoreWorkDays(userInfo.getPartnerUserInfo().getStoreId());
		BigDecimal workEffectTotal = OperationUtil.divideHandler(saleTotal, workDays);
		runReportResult.setWorkEffectTotal(workEffectTotal);
		// 上个月大店目标劳效额
		BigDecimal workEffectTarget = runIndexDao
				.getLastMonthStoreWorkRatioTarget(userInfo.getPartnerUserInfo().getStoreId());
		runReportResult.setWorkEffectTarget(workEffectTarget);
		BigDecimal levelRatio = BigDecimal.ZERO;
		if (workEffectTarget.compareTo(BigDecimal.ZERO) != 0) {
			levelRatio = OperationUtil.divideHandler(workEffectTotal, workEffectTarget).multiply(new BigDecimal(100));
		}
		runReportResult.setLevelRatio(levelRatio.toString() + "%");
		// 标准档位
		List<WorkLevelConfig> configList = runIndexDao
				.getWorkLevelConfigList(userInfo.getPartnerUserInfo().getGroupId());
		for (WorkLevelConfig workLevelConfig : configList) {
			if ((levelRatio.compareTo(workLevelConfig.getMinCompletRate()) == 1
					|| levelRatio.compareTo(workLevelConfig.getMinCompletRate()) == 0)
					&& levelRatio.compareTo(workLevelConfig.getMaxCompletRate()) == -1) {
				runReportResult.setLevelName(workLevelConfig.getLevelName());
				break;
			}
		}
		runReportResult.setAreaUnit("平米");
		runReportResult.setEChargeUnit("元");
		runReportResult.setMachineUnit("台");
		runReportResult.setPersonUnit("人");
		return runReportResult;
	}

	@Override
	public List<RunWorkEffect> getRunWorkEffectList(UserInfo userInfo, String beginYm, String endYm) throws Exception {
		// 选择区间月的大店销售额
		List<RunBaseReport> saleList = runIndexDao.getScopeStoreSale(beginYm, endYm,
				userInfo.getPartnerUserInfo().getStoreId());
		// 选择区间月的大店人员总时长
		List<RunBaseReport> workDaysList = runIndexDao.getScopeStoreWorkDays(beginYm, endYm,
				userInfo.getPartnerUserInfo().getStoreId());
		// 大店选择区间月的目标劳效
		List<Goal> goalList = goalDao.getScopeWorkRatioTarget(userInfo.getPartnerUserInfo().getStoreId(), beginYm,
				endYm);
		Map<String, BigDecimal> workDaysMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> goalMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> saleMap = new HashMap<String, BigDecimal>();
		workDaysList.stream().forEach(runBaseReport -> {
			workDaysMap.put(runBaseReport.getTime(), runBaseReport.getValue());
		});
		List<String> ymList = DateUtil.getMonthBetween(beginYm, endYm);
		goalList.stream().forEach(goal -> {
			goalMap.put(goal.getGoalYm(), goal.getSubValues());
		});
		saleList.stream().forEach(sale -> {
			saleMap.put(sale.getTime(), sale.getValue());
		});
		List<RunWorkEffect> runWorkEffectList = Lists.newArrayList();
		List<RunWorkEffect> runWorkEffectListResult = Lists.newArrayList();
		ymList.stream().forEach(ym -> {
			RunWorkEffect runWorkEffect = new RunWorkEffect();
			BigDecimal value = BigDecimal.ZERO;
			BigDecimal ratio = BigDecimal.ZERO;
			BigDecimal days = BigDecimal.ZERO;
			BigDecimal goal = BigDecimal.ZERO;
			if (saleMap.get(ym) != null) {
				value = saleMap.get(ym);
			}
			if (workDaysMap.get(ym) != null) {
				days = workDaysMap.get(ym);
			}
			if (goalMap.get(ym) != null) {
				goal = goalMap.get(ym);
			}
			if (days.compareTo(BigDecimal.ZERO) == 1) {
				value = value.divide(days, 2, BigDecimal.ROUND_HALF_UP);
			}
			if (goal.compareTo(BigDecimal.ZERO) == 1) {
				ratio = value.divide(goal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
			}
			runWorkEffect.setTime(ym);
			runWorkEffect.setValue(OperationUtil.divideHandler(value, new BigDecimal(1000)));
			runWorkEffect.setRatio(ratio);
			runWorkEffectList.add(runWorkEffect);
		});
		if (runWorkEffectList.size() > 0) {// 按照时间排序
			runWorkEffectListResult = runWorkEffectList.stream().sorted(Comparator.comparing(RunWorkEffect::getCompare))
					.collect(Collectors.toList());
		}
		return runWorkEffectListResult;
	}

}

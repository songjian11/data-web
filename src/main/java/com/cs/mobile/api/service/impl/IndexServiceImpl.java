package com.cs.mobile.api.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.dao.IndexDao;
import com.cs.mobile.api.model.partner.battle.Accounting;
import com.cs.mobile.api.model.partner.progress.CostVal;
import com.cs.mobile.api.model.partner.progress.ProgressReport;
import com.cs.mobile.api.model.partner.progress.ShareDetail;
import com.cs.mobile.api.service.IndexService;

@Service
public class IndexServiceImpl implements IndexService {

	@Autowired
	IndexDao indexDao;

	@Override
	public ProgressReport getIndexSale(Map<String, Object> paramMap) throws Exception {
		return indexDao.getIndexSale(paramMap);
	}

	@Override
	public ProgressReport getIndexFontGp(Map<String, Object> paramMap) throws Exception {
		return indexDao.getIndexFontGp(paramMap);
	}

	@Override
	public ProgressReport getIndexAfterGp(Map<String, Object> paramMap) throws Exception {
		return indexDao.getIndexAfterGp(paramMap);
	}

	@Override
	public ProgressReport getIndexCost(Map<String, Object> paramMap) throws Exception {
		return indexDao.getIndexCost(paramMap);
	}

	@Override
	public BigDecimal getIndexShare(Map<String, Object> paramMap) throws Exception {
		return indexDao.getIndexShare(paramMap);
	}

	/**
	 * 分享-明细
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public ShareDetail getShareDetail(Map<String, Object> paramMap) throws Exception {
		ShareDetail shareDetail = new ShareDetail();
		BigDecimal proportion = indexDao.getIndexShare(paramMap);// 分配比例
		ProgressReport fontGp = indexDao.getIndexFontGp(paramMap);// 前台毛利 实际，目标
		ProgressReport afterGp = indexDao.getIndexAfterGp(paramMap);// 后台毛利 实际，目标
		ProgressReport countGp = new ProgressReport("总毛利", fontGp.getActualVal().add(afterGp.getActualVal()),
				fontGp.getGoalVal().add(afterGp.getGoalVal()));// 总毛利

		CostVal costActual = indexDao.getCostActualList(paramMap);// 各项费用 实际
		CostVal costGoal = indexDao.getCostGoalList(paramMap);// 各项费用 目标

		BigDecimal attract = indexDao.getCostGoalAttract(paramMap);// 招商 目标（实际为0）
		BigDecimal stockActual = indexDao.getCostActualStock(paramMap);// 库存 实际
		BigDecimal stockGoal = indexDao.getCostGoalStock(paramMap);// 库存 目标

		BigDecimal incomeCount = attract.add(fontGp.getActualVal()).add(afterGp.getActualVal());// 收入合计=招商+前台毛利+后台毛利
		// 实际费用合计
		BigDecimal costActualCount = stockActual.add(costActual.getDepreciation()).add(costActual.getHydropower())
				.add(costActual.getLease()).add(costActual.getManpower()).add(costActual.getOther());
		// 目标费用合计
		BigDecimal costGoalCount = stockGoal.add(costGoal.getDepreciation()).add(costGoal.getHydropower())
				.add(costGoal.getLease()).add(costGoal.getManpower()).add(costGoal.getOther());

		BigDecimal excessProfit = (countGp.getActualVal().subtract(costActualCount))
				.subtract((countGp.getGoalVal().subtract(costGoalCount)));// 超额利润=实际利润-目标利润 （利润=总毛利-总费用）
		BigDecimal shareVal = excessProfit.multiply(proportion).multiply(new BigDecimal(10000)).setScale(0,
				BigDecimal.ROUND_HALF_UP);// 分享金额=超额利润*分享比例

		shareDetail.setSale(indexDao.getIndexSale(paramMap).getActualVal());// 销售 实际
		shareDetail.setAttract(attract);// 招商
		shareDetail.setCostList(costActual);// 各项费用
		shareDetail.setStock(stockActual);// 库存
		shareDetail.setFrontGp(fontGp.getActualVal());// 前台毛利
		shareDetail.setAfterGp(afterGp.getActualVal());// 后台毛利
		shareDetail.setCountGp(countGp.getActualVal());// 总毛利
		shareDetail.setExcessProfit(excessProfit);// 超额利润=总毛利-总费用
		shareDetail.setShareval(excessProfit.multiply(proportion));// 分享金额=超额利润*分享比例
		shareDetail.setIncomeCount(incomeCount);// 收入合计
		shareDetail.setCostCount(costActualCount);// 费用合计=库存+人力+其他+营业+折旧+水电+租赁
		shareDetail.setCheckProfit(incomeCount.subtract(costActualCount));// 考核利润=收入合计-费用合计
		shareDetail.setShareval(1 == shareVal.compareTo(new BigDecimal(0)) ? shareVal : new BigDecimal(0));// 分享金额=超额利润*分配比例
		return shareDetail;
	}

	/**
	 * 核算标准
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> getAccountingDesc(Map<String, Object> paramMap) throws Exception {
		Map rsMap = new HashMap<String, Object>();
		// add + ,subtract - ,multiply * , divide /
		ProgressReport sale = indexDao.getIndexSale(paramMap);// 销售 实际，目标
		ProgressReport fontGp = indexDao.getIndexFontGp(paramMap);// 前台毛利 实际，目标
		ProgressReport afterGp = indexDao.getIndexAfterGp(paramMap);// 后台毛利 实际，目标

		CostVal costActual = indexDao.getCostActualList(paramMap);// 各项费用 实际
		CostVal costGoal = indexDao.getCostGoalList(paramMap);// 各项费用 目标

		BigDecimal proportion = indexDao.getIndexShare(paramMap);// 分配比例
		BigDecimal stockActual = indexDao.getCostActualStock(paramMap);// 库存 实际
		BigDecimal stockGoal = indexDao.getCostGoalStock(paramMap);// 库存 目标
		BigDecimal attractGoal = indexDao.getCostGoalAttract(paramMap);// 招商 目标（实际为0）
		BigDecimal incomeActualCount = attractGoal.add(fontGp.getActualVal()).add(afterGp.getActualVal());// 实际收入合计=招商+前台毛利+后台毛利
		BigDecimal incomeGoalCount = fontGp.getGoalVal().add(afterGp.getGoalVal());// 目标收入合计=招商(0)+前台毛利+后台毛利
		// 实际费用合计
		BigDecimal costActualCount = stockActual.add(costActual.getDepreciation()).add(costActual.getHydropower())
				.add(costActual.getLease()).add(costActual.getManpower()).add(costActual.getOther());
		// 目标费用合计
		BigDecimal costGoalCount = stockGoal.add(costGoal.getDepreciation()).add(costGoal.getHydropower())
				.add(costGoal.getLease()).add(costGoal.getManpower()).add(costGoal.getOther());

		ProgressReport attract = new ProgressReport("招商", null, attractGoal);// 招商
		ProgressReport countGp = new ProgressReport("总毛利", fontGp.getActualVal().add(afterGp.getActualVal()),
				fontGp.getGoalVal().add(afterGp.getGoalVal()));// 总毛利
		ProgressReport manpower = new ProgressReport("人力成本", costActual.getManpower(), costGoal.getManpower());// 人力成本
		ProgressReport depreciation = new ProgressReport("折旧", costActual.getDepreciation(),
				costGoal.getDepreciation());// 折旧
		ProgressReport hydropower = new ProgressReport("水电", costActual.getHydropower(), costGoal.getHydropower());// 水电
		ProgressReport lease = new ProgressReport("租赁", costActual.getLease(), costGoal.getLease());// 租赁
		ProgressReport other = new ProgressReport("其他", costActual.getOther(), costGoal.getOther());// 其他
		ProgressReport stock = new ProgressReport("库存", stockActual, stockGoal);// 库存
		ProgressReport checkProfit = new ProgressReport("考核利润", incomeActualCount.subtract(costActualCount),
				incomeGoalCount.subtract(costGoalCount));// 考核利润

		ProgressReport excessProfit = new ProgressReport("超额利润", (countGp.getActualVal().subtract(costActualCount))
				.subtract((countGp.getGoalVal().subtract(costGoalCount))), null);// 超额利润=实际利润-目标利润 （利润=总毛利-总费用）
		BigDecimal share = excessProfit.getActualVal().multiply(proportion).multiply(new BigDecimal(10000)).setScale(0,
				BigDecimal.ROUND_HALF_UP);// 分享金额-元
		ProgressReport shareVal = new ProgressReport("分享金额",
				1 == share.compareTo(new BigDecimal(0)) ? share : new BigDecimal(0), null);// 分享金额=超额利润*分享比例
		shareVal.setUnit("元");

		rsMap.put("sale", sale);// 销售
		rsMap.put("fontGp", fontGp);// 前台毛利
		rsMap.put("afterGp", afterGp);// 后台毛利
		rsMap.put("attract", attract);// 招商
		rsMap.put("countGp", countGp);// 总毛利

		rsMap.put("manpower", manpower);// 人力成本
		rsMap.put("depreciation", depreciation);// 折旧
		rsMap.put("hydropower", hydropower);// 水电
		rsMap.put("lease", lease);// 租赁
		rsMap.put("other", other);// 其他
		rsMap.put("stock", stock);// 库存

		rsMap.put("checkProfit", checkProfit);// 考核利润
		rsMap.put("excessProfit", excessProfit);// 超额利润
		rsMap.put("shareVal", shareVal);// 分享金额

		rsMap.put("incomeCount", incomeActualCount);// 收入合计
		rsMap.put("costCount", costActualCount);// 费用合计=库存+人力+其他+营业+折旧+水电+租赁
		rsMap.put("unit", "万");
		return rsMap;
	}

	@Override
	public List<Accounting> getAccountingList(Map<String, Object> paramMap) throws Exception {
		return indexDao.getAccountingList(paramMap);
	}
}

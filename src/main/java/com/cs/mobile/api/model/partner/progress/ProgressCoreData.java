package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.api.service.partner.progress.impl.ComProgressServiceImpl;
import com.cs.mobile.common.utils.OperationUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@ApiModel(value = "ProgressCoreData", description = "首页进度核心数据对象")
public class ProgressCoreData implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "门店", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店", required = false)
	private String comId;
	@ApiModelProperty(value = "实际销售额", required = false)
	private BigDecimal actualSale = BigDecimal.ZERO;
	@ApiModelProperty(value = "月总销售目标", required = false)
	private BigDecimal saleTotalGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "当前累计目标销售额", required = false)
	private BigDecimal curSaleGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "销售差额", required = false)
	private BigDecimal saleDiffVal = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际总费用", required = false)
	private BigDecimal actualTotalCost = BigDecimal.ZERO;
	@ApiModelProperty(value = "月总费用目标", required = false)
	private BigDecimal costTotalGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "当前累计目标费用额", required = false)
	private BigDecimal curCostGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "费用差额", required = false)
	private BigDecimal costDiffVal = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际前台毛利", required = false)
	private BigDecimal actualFrontGp = BigDecimal.ZERO;
	@ApiModelProperty(value = "当前累计目标前台毛利", required = false)
	private BigDecimal curFrontGpGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际后台收入", required = false)
	private BigDecimal actualAfterGp = BigDecimal.ZERO;
	@ApiModelProperty(value = "月毛利额(即前台毛利)目标", required = false)
	private BigDecimal frontGpTotalGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "月总DC成本目标", required = false)
	private BigDecimal dcTotalGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "月后台目标", required = false)
	private BigDecimal afterGpTotalGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "月招商收入目标", required = false)
	private BigDecimal attractTotalGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "当前累计目标后台收入", required = false)
	private BigDecimal curAfterGpGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际总毛利", required = false)
	private BigDecimal actualTotalGp = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际招商收入", required = false)
	private BigDecimal actualAttract = BigDecimal.ZERO;
	@ApiModelProperty(value = "当前累计目标招商收入", required = false)
	private BigDecimal curAttractGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "月总毛利目标", required = false)
	private BigDecimal gpTotalGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "当前累计目标毛利额", required = false)
	private BigDecimal curGpGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "毛利差额", required = false)
	private BigDecimal gpDiffVal = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际总利润", required = false)
	private BigDecimal actualTotalProfit = BigDecimal.ZERO;
	@ApiModelProperty(value = "月总利润目标", required = false)
	private BigDecimal profitTotalGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "当前累计目标利润额", required = false)
	private BigDecimal curProfitGoal = BigDecimal.ZERO;
	@ApiModelProperty(value = "利润差额", required = false)
	private BigDecimal profitDiffVal = BigDecimal.ZERO;
	@ApiModelProperty(value = "各项费用当前累计目标", required = false)
	private CostVal curCostGoalObject = new CostVal();
	@ApiModelProperty(value = "实际库存占用资金", required = false)
	private BigDecimal actualStock = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际销售总成本", required = false)
	private BigDecimal actualSaleCost = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际销售完成率", required = false)
	private BigDecimal actualSaleRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "目标销售完成率", required = false)
	private BigDecimal goalSaleRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "销售差异完成率", required = false)
	private BigDecimal diffSaleRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际费用完成率", required = false)
	private BigDecimal actualCostRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "目标费用完成率", required = false)
	private BigDecimal goalCostRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "费用差异完成率", required = false)
	private BigDecimal diffCostRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际毛利完成率", required = false)
	private BigDecimal actualGpRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "目标毛利完成率", required = false)
	private BigDecimal goalGpRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "毛利差异完成率", required = false)
	private BigDecimal diffGpRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "实际利润完成率", required = false)
	private BigDecimal actualProfitRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "目标利润完成率", required = false)
	private BigDecimal goalProfitRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "利润差异完成率", required = false)
	private BigDecimal diffProfitRatio = BigDecimal.ZERO;
	@ApiModelProperty(value = "超额利润", required = false)
	private BigDecimal excessProfit = BigDecimal.ZERO;
	@ApiModelProperty(value = "预估分享金额", required = false)
	private BigDecimal share = BigDecimal.ZERO;

	/**
	 * 计算比率
	 * 
	 * @author wells
	 * @param totalDays
	 * @param curDays
	 * @time 2018年12月24日
	 */
	public ProgressCoreData calculateRatio(int totalDays, int curDays) {
		BigDecimal goalRatio = OperationUtil.divideHandler(new BigDecimal(curDays), new BigDecimal(totalDays))
				.multiply(new BigDecimal(100));
		// 销售
		this.goalSaleRatio = goalRatio;
		if (this.getSaleTotalGoal().compareTo(BigDecimal.ZERO) == -1) {// 目标小于0
			// 2-实际完成数/目标完成数
			this.actualSaleRatio = new BigDecimal(2)
					.subtract(OperationUtil.divideHandler(this.getActualSale(), this.getSaleTotalGoal()))
					.multiply(new BigDecimal(100));
			this.diffSaleRatio = this.actualSaleRatio.subtract(this.goalSaleRatio);
		} else {// 目标大于0
			this.actualSaleRatio = OperationUtil.divideHandler(this.getActualSale(), this.getSaleTotalGoal())
					.multiply(new BigDecimal(100));
			this.diffSaleRatio = OperationUtil.divideHandler(this.getSaleDiffVal(), this.getSaleTotalGoal())
					.multiply(new BigDecimal(100));
		}

		// 费用
		this.goalCostRatio = goalRatio;
		if (this.getCostTotalGoal().compareTo(BigDecimal.ZERO) == -1) {// 目标小于0
			// 2-实际完成数/目标完成数
			this.actualCostRatio = new BigDecimal(2)
					.subtract(OperationUtil.divideHandler(this.getActualTotalCost(), this.getCostTotalGoal()))
					.multiply(new BigDecimal(100));
			this.diffCostRatio = this.actualCostRatio.subtract(this.goalCostRatio);
		} else {// 目标大于0
			this.actualCostRatio = OperationUtil.divideHandler(this.getActualTotalCost(), this.getCostTotalGoal())
					.multiply(new BigDecimal(100));
			this.diffCostRatio = OperationUtil.divideHandler(this.getCostDiffVal(), this.getCostTotalGoal())
					.multiply(new BigDecimal(100));
		}

		// 毛利
		this.goalGpRatio = goalRatio;
		if (this.getGpTotalGoal().compareTo(BigDecimal.ZERO) == -1) {// 目标小于0
			// 2-实际完成数/目标完成数
			this.actualGpRatio = new BigDecimal(2)
					.subtract(OperationUtil.divideHandler(this.getActualTotalGp(), this.getGpTotalGoal()))
					.multiply(new BigDecimal(100));
			this.diffGpRatio = this.actualGpRatio.subtract(this.goalGpRatio);
		} else {// 目标大于0
			this.actualGpRatio = OperationUtil.divideHandler(this.getActualTotalGp(), this.getGpTotalGoal())
					.multiply(new BigDecimal(100));
			this.diffGpRatio = OperationUtil.divideHandler(this.getGpDiffVal(), this.getGpTotalGoal())
					.multiply(new BigDecimal(100));
		}

		// 利润
		this.goalProfitRatio = goalRatio;
		if (this.getProfitTotalGoal().compareTo(BigDecimal.ZERO) == -1) {// 目标小于0
			// 2-实际完成数/目标完成数
			this.actualProfitRatio = new BigDecimal(2)
					.subtract(OperationUtil.divideHandler(this.getActualTotalProfit(), this.getProfitTotalGoal()))
					.multiply(new BigDecimal(100));
			this.diffProfitRatio = this.actualProfitRatio.subtract(this.goalProfitRatio);
		} else {// 目标大于0
			this.actualProfitRatio = OperationUtil.divideHandler(this.getActualTotalProfit(), this.getProfitTotalGoal())
					.multiply(new BigDecimal(100));
			this.diffProfitRatio = OperationUtil.divideHandler(this.getProfitDiffVal(), this.getProfitTotalGoal())
					.multiply(new BigDecimal(100));
		}
		return this;
	}

	public void addActualSale(BigDecimal item) {
		this.actualSale = this.actualSale.add(item);
	}

	public void addActualFrontGp(BigDecimal item) {
		this.actualFrontGp = this.actualFrontGp.add(item);
	}

	public void addActualAfterGp(BigDecimal item) {
		this.actualAfterGp = this.actualAfterGp.add(item);
	}

	public void addActualSaleCost(BigDecimal item) {
		this.actualSaleCost = this.actualSaleCost.add(item);
	}

	public void addActualTotalGp(BigDecimal item) {
		this.actualTotalGp = this.actualTotalGp.add(item);
	}

	public void addActualStock(BigDecimal item) {
		this.actualStock = this.actualStock.add(item);
	}

	public void addSaleTotalGoal(BigDecimal item) {
		this.saleTotalGoal = this.saleTotalGoal.add(item);
	}

	public void addCurSaleGoal(BigDecimal item) {
		this.curSaleGoal = this.curSaleGoal.add(item);
	}

	public void addSaleDiffVal(BigDecimal item) {
		this.saleDiffVal = this.saleDiffVal.add(item);
	}

	public void addActualTotalCost(BigDecimal item) {
		this.actualTotalCost = this.actualTotalCost.add(item);
	}

	public void addCostTotalGoal(BigDecimal item) {
		this.costTotalGoal = this.costTotalGoal.add(item);
	}

	public void addCurCostGoal(BigDecimal item) {
		this.curCostGoal = this.curCostGoal.add(item);
	}

	public void addCostDiffVal(BigDecimal item) {
		this.costDiffVal = this.costDiffVal.add(item);
	}

	public void addCurFrontGpGoal(BigDecimal item) {
		this.curFrontGpGoal = this.curFrontGpGoal.add(item);
	}

	public void addFrontGpTotalGoal(BigDecimal item) {
		this.frontGpTotalGoal = this.frontGpTotalGoal.add(item);
	}

	public void addDcTotalGoal(BigDecimal item) {
		this.dcTotalGoal = this.dcTotalGoal.add(item);
	}

	public void addAfterGpTotalGoal(BigDecimal item) {
		this.afterGpTotalGoal = this.afterGpTotalGoal.add(item);
	}

	public void addAttractTotalGoal(BigDecimal item) {
		this.attractTotalGoal = this.attractTotalGoal.add(item);
	}

	public void addCurAfterGpGoal(BigDecimal item) {
		this.curAfterGpGoal = this.curAfterGpGoal.add(item);
	}

	public void addCurAttractGoal(BigDecimal item) {
		this.curAttractGoal = this.curAttractGoal.add(item);
	}

	public void addGpTotalGoal(BigDecimal item) {
		this.gpTotalGoal = this.gpTotalGoal.add(item);
	}

	public void addCurGpGoal(BigDecimal item) {
		this.curGpGoal = this.curGpGoal.add(item);
	}

	public void addGpDiffVal(BigDecimal item) {
		this.gpDiffVal = this.gpDiffVal.add(item);
	}

	public void addActualTotalProfit(BigDecimal item) {
		this.actualTotalProfit = this.actualTotalProfit.add(item);
	}

	public void addProfitTotalGoal(BigDecimal item) {
		this.profitTotalGoal = this.profitTotalGoal.add(item);
	}

	public void addCurProfitGoal(BigDecimal item) {
		this.curProfitGoal = this.curProfitGoal.add(item);
	}

	public void addProfitDiffVal(BigDecimal item) {
		this.profitDiffVal = this.profitDiffVal.add(item);
	}

	public void addExcessProfit(BigDecimal item) {
		this.excessProfit = this.excessProfit.add(item);
	}

	public void addShare(BigDecimal item) {
		this.share = this.share.add(item);
	}

	public void operation(BigDecimal distributionValue) {
		actualTotalCost = curCostGoal.add(actualStock);// 实际总费用=总费用+库存资金
		saleDiffVal = actualSale.subtract(curSaleGoal).setScale(2, BigDecimal.ROUND_HALF_UP);
		gpDiffVal = actualTotalGp.subtract(curGpGoal).setScale(2, BigDecimal.ROUND_HALF_UP);
		costDiffVal = actualTotalCost.subtract(curCostGoal).setScale(2, BigDecimal.ROUND_HALF_UP);
		// 实际利润=实际总毛利-实际总费用
		actualTotalProfit = actualTotalGp.subtract(actualTotalCost);
		// 月目标利润=月目标总毛利+月目标招商收入-月目标总费用
		profitTotalGoal = gpTotalGoal.add(attractTotalGoal).subtract(costTotalGoal);
		// 当前目标利润=当前目标总毛利+当前目标招商收入-当前目标总费用
		curProfitGoal = curGpGoal.add(curAttractGoal).subtract(curCostGoal);
		profitDiffVal = actualTotalProfit.subtract(curProfitGoal);
		actualAttract = BigDecimal.ZERO;// 实际招商收入为0
		// 超额利润=实际利润额-目标利润额
		excessProfit = actualTotalProfit.subtract(curProfitGoal);
		// 预估分享金额=超额利润*分享比例 大店进入的话，即是后勤类型的小店
		BigDecimal share = excessProfit.multiply(distributionValue == null ? BigDecimal.ZERO : distributionValue)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		if (profitTotalGoal.compareTo(BigDecimal.ZERO) == 0) {// 如果目标利润为0，预估分享金额为0
			this.share = BigDecimal.ZERO;
		} else {
			this.share = share.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : share;// 预估分享金额小于0的时候为0
		}
	}

}

package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AccountingItemResult", description = "核算明细对象")
public class AccountingItemResult implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "销售", required = false)
	private ProgressReport sale;
	@ApiModelProperty(value = "前台毛利", required = false)
	private ProgressReport fontGp;
	@ApiModelProperty(value = "后台毛利", required = false)
	private ProgressReport afterGp;
	@ApiModelProperty(value = "招商", required = false)
	private ProgressReport attract;
	@ApiModelProperty(value = "总毛利", required = false)
	private ProgressReport countGp;
	@ApiModelProperty(value = "人力成本", required = false)
	private ProgressReport manpower;
	@ApiModelProperty(value = "折旧", required = false)
	private ProgressReport depreciation;
	@ApiModelProperty(value = "水电", required = false)
	private ProgressReport hydropower;
	@ApiModelProperty(value = "租赁", required = false)
	private ProgressReport lease;
	@ApiModelProperty(value = "其他", required = false)
	private ProgressReport other;
	@ApiModelProperty(value = "库存", required = false)
	private ProgressReport stock;
	@ApiModelProperty(value = "考核利润", required = false)
	private ProgressReport checkProfit;
	@ApiModelProperty(value = "超额利润", required = false)
	private ProgressReport excessProfit;
	@ApiModelProperty(value = "分享金额", required = false)
	private ProgressReport shareVal;
	@ApiModelProperty(value = "收入合计", required = false)
	private BigDecimal incomeCount;
	@ApiModelProperty(value = "费用合计", required = false)
	private BigDecimal costCount;
	@ApiModelProperty(value = "单位", required = false)
	private String unit;

	@ApiModelProperty(value = "目标收入合计", required = false)
	private BigDecimal incomeGoalCount;
	@ApiModelProperty(value = "目标费用合计", required = false)
	private BigDecimal costGoalCount;
	@ApiModelProperty(value = "目标利润", required = false)
	private BigDecimal goalProfit;

	public AccountingItemResult(ProgressReport sale, ProgressReport fontGp, ProgressReport afterGp,
			ProgressReport attract, ProgressReport countGp, ProgressReport manpower, ProgressReport depreciation,
			ProgressReport hydropower, ProgressReport lease, ProgressReport other, ProgressReport stock,
			ProgressReport checkProfit, ProgressReport excessProfit, ProgressReport shareVal, BigDecimal incomeCount,
			BigDecimal costCount, String unit) {
		super();
		this.sale = sale;
		this.fontGp = fontGp;
		this.afterGp = afterGp;
		this.attract = attract;
		this.countGp = countGp;
		this.manpower = manpower;
		this.depreciation = depreciation;
		this.hydropower = hydropower;
		this.lease = lease;
		this.other = other;
		this.stock = stock;
		this.checkProfit = checkProfit;
		this.excessProfit = excessProfit;
		this.shareVal = shareVal;
		this.incomeCount = incomeCount;
		this.costCount = costCount;
		this.unit = unit;
	}

	public AccountingItemResult(){}

	public BigDecimal getIncomeGoalCount() {
		return countGp.getGoalVal().add(attract.getGoalVal());
	}

	public BigDecimal getCostGoalCount() {
		return stock.getGoalVal().add(manpower.getGoalVal()).add(depreciation.getGoalVal()).
				add(hydropower.getGoalVal()).add(lease.getGoalVal()).add(other.getGoalVal());
	}

	public BigDecimal getGoalProfit() {
		return countGp.getGoalVal().subtract(
				stock.getGoalVal().add(manpower.getGoalVal()).add(depreciation.getGoalVal()).
				add(hydropower.getGoalVal()).add(lease.getGoalVal()).add(other.getGoalVal())
		);
	}
}

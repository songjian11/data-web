package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.common.utils.OperationUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ShareDetail", description = "分享明细")
public class ShareDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "销售", required = false)
	private BigDecimal sale;

	@ApiModelProperty(value = "招商", required = false)
	private BigDecimal attract;

	@ApiModelProperty(value = "各项成本费用", required = false)
	private CostVal costList;

	@ApiModelProperty(value = "库存", required = false)
	private BigDecimal stock;

	@ApiModelProperty(value = "后台毛利", required = false)
	private BigDecimal afterGp;

	@ApiModelProperty(value = "前台毛利", required = false)
	private BigDecimal frontGp;

	@ApiModelProperty(value = "总毛利", required = false)
	private BigDecimal countGp;

	@ApiModelProperty(value = "超额利润", required = false)
	private BigDecimal excessProfit;

	@ApiModelProperty(value = "分享金额", required = false)
	private BigDecimal shareval;

	@ApiModelProperty(value = "收入合计", required = false)
	private BigDecimal incomeCount;

	@ApiModelProperty(value = "费用合计", required = false)
	private BigDecimal costCount;

	@ApiModelProperty(value = "考核利润", required = false)
	private BigDecimal checkProfit;

	@ApiModelProperty(value = "单位", required = false)
	private String unit = "万";

	@ApiModelProperty(value = "分享金额单位", required = false)
	private String shareUnit = "元";

	public ShareDetail(BigDecimal sale, BigDecimal attract, CostVal costList, BigDecimal stock, BigDecimal afterGp,
			BigDecimal frontGp, BigDecimal countGp, BigDecimal excessProfit, BigDecimal shareval,
			BigDecimal incomeCount, BigDecimal costCount, BigDecimal checkProfit, String unit, String shareUnit) {
		super();
		this.sale = sale.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.attract = attract.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.costList = costList;
		this.stock = stock.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.afterGp = afterGp.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.frontGp = frontGp.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.countGp = countGp.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.excessProfit = excessProfit.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.shareval = shareval.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.incomeCount = incomeCount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.costCount = costCount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.checkProfit = checkProfit.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.unit = unit;
		this.shareUnit = shareUnit;
	}

	public ShareDetail() {
		super();
	}

	public void add(BigDecimal sale, BigDecimal attract, BigDecimal stock, BigDecimal afterGp, BigDecimal frontGp,
			BigDecimal countGp, BigDecimal excessProfit, BigDecimal shareval, BigDecimal incomeCount,
			BigDecimal costCount, BigDecimal checkProfit) {
		this.sale = (this.sale == null ? BigDecimal.ZERO : this.sale).add(sale);
		this.attract = (this.attract == null ? BigDecimal.ZERO : this.attract).add(attract);
		this.stock = (this.stock == null ? BigDecimal.ZERO : this.stock).add(stock);
		this.afterGp = (this.afterGp == null ? BigDecimal.ZERO : this.afterGp).add(afterGp);
		this.frontGp = (this.frontGp == null ? BigDecimal.ZERO : this.frontGp).add(frontGp);
		this.countGp = (this.countGp == null ? BigDecimal.ZERO : this.countGp).add(countGp);
		this.excessProfit = (this.excessProfit == null ? BigDecimal.ZERO : this.excessProfit).add(excessProfit);
		this.shareval = (this.shareval == null ? BigDecimal.ZERO : this.shareval).add(shareval);
		this.incomeCount = (this.incomeCount == null ? BigDecimal.ZERO : this.incomeCount).add(incomeCount);
		this.costCount = (this.costCount == null ? BigDecimal.ZERO : this.costCount).add(costCount);
		this.checkProfit = (this.checkProfit == null ? BigDecimal.ZERO : this.checkProfit).add(checkProfit);
	}

	public void unitConvert(BigDecimal divisor) {
		this.sale = OperationUtil.divideHandler(this.sale, divisor);
		this.attract = OperationUtil.divideHandler(this.attract, divisor);
		this.stock = OperationUtil.divideHandler(this.stock, divisor);
		this.afterGp = OperationUtil.divideHandler(this.afterGp, divisor);
		this.frontGp = OperationUtil.divideHandler(this.frontGp, divisor);
		this.countGp = OperationUtil.divideHandler(this.countGp, divisor);
		this.excessProfit = OperationUtil.divideHandler(this.excessProfit, divisor);
		this.shareval = OperationUtil.divideHandler(this.shareval, divisor);
		this.incomeCount = OperationUtil.divideHandler(this.incomeCount, divisor);
		this.costCount = OperationUtil.divideHandler(this.costCount, divisor);
		this.checkProfit = OperationUtil.divideHandler(this.checkProfit, divisor);
	}
}

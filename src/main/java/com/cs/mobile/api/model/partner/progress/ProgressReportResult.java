package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ProgressReportResult", description = "首页进度报表返回对象")
public class ProgressReportResult implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "销售", required = false)
	private ProgressReport sale;
	@ApiModelProperty(value = "毛利", required = false)
	private ProgressReport gp;
	@ApiModelProperty(value = "利润", required = false)
	private ProgressReport profit;
	@ApiModelProperty(value = "费用", required = false)
	private ProgressReport cost;
	@ApiModelProperty(value = "分享金额", required = false)
	private BigDecimal share;
	@ApiModelProperty(value = "分享金额单位", required = false)
	private String shareUnit;

	public ProgressReportResult(ProgressReport gp, ProgressReport profit, ProgressReport cost, ProgressReport sale,
			BigDecimal share, String shareUnit) {
		super();
		this.gp = gp;
		this.profit = profit;
		this.cost = cost;
		this.sale = sale;
		this.share = share;
		this.shareUnit = shareUnit;
	}
}

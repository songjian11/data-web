package com.cs.mobile.api.model.partner.battle;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Accounting", description = "核算表格")
public class Accounting implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "月份", required = false)
	private String ymonth;

	@ApiModelProperty(value = "销售", required = false)
	private BigDecimal saleval;

	@ApiModelProperty(value = "招商", required = false)
	private BigDecimal attractval;

	@ApiModelProperty(value = "费用", required = false)
	private BigDecimal acostval;

	@ApiModelProperty(value = "利润", required = false)
	private BigDecimal aprofitval;

	@ApiModelProperty(value = "利润率", required = false)
	private BigDecimal profitrate;

	@ApiModelProperty(value = "损耗率", required = false)
	private BigDecimal lossrate;

	@ApiModelProperty(value = "前台毛利率", required = false)
	private BigDecimal gprate;

	@ApiModelProperty(value = "分享金额", required = false)
	private BigDecimal shareval;

	@ApiModelProperty(value = "单位", required = false)
	private String unit = "万";

	@ApiModelProperty(value = "分享金额单位", required = false)
	private String shareUnit = "元";

	public BigDecimal getSaleval() {
		return null == saleval ? new BigDecimal(0) : saleval.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getAttractval() {
		return null == attractval ? new BigDecimal(0) : attractval.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getAcostval() {
		return null == acostval ? new BigDecimal(0) : acostval.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getAprofitval() {
		return null == aprofitval ? new BigDecimal(0) : aprofitval.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getProfitrate() {
		return null == profitrate ? new BigDecimal(0) : profitrate.setScale(0, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getLossrate() {
		return null == lossrate ? new BigDecimal(0) : lossrate.setScale(0, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getGprate() {
		return null == gprate ? new BigDecimal(0) : gprate.setScale(0, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getShareval() {
		return null == shareval ? new BigDecimal(0)
				: (1 == shareval.compareTo(new BigDecimal(0)) ? shareval.setScale(0, BigDecimal.ROUND_HALF_DOWN)
						: new BigDecimal(0));
	}
}

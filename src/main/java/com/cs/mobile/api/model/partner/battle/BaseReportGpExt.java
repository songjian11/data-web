package com.cs.mobile.api.model.partner.battle;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BaseReportGpExt", description = "基本报表毛利扩展对象")
public class BaseReportGpExt extends BaseReport {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "前台毛利", required = false)
	private BigDecimal gpValue;
	@ApiModelProperty(value = "销售成本", required = false)
	private BigDecimal saleCost;
}

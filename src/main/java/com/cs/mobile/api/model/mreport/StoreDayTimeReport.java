package com.cs.mobile.api.model.mreport;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreDayTimeReport", description = "门店时段报表基本对象")
public class StoreDayTimeReport implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "时段", required = true)
	private String time;
	@ApiModelProperty(value = "未税销售金额", required = true)
	private BigDecimal saleValue;
	@ApiModelProperty(value = "客流", required = true)
	private Long pfCount;
	@ApiModelProperty(value = "客单", required = true)
	private BigDecimal perPrice;
	@ApiModelProperty(value = "含税扫描毛利", required = true)
	private BigDecimal sgp;
	@ApiModelProperty(value = "未税扫描毛利", required = true)
	private BigDecimal sgpEcl;
	@ApiModelProperty(value = "扫描毛利率", required = true)
	private String sgpRatio;
	@ApiModelProperty(value = "含税销售金额", required = true)
	private BigDecimal saleValueIn;

}

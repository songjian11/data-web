package com.cs.mobile.api.model.mreport;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreDayDeptReport", description = "门店大类报表基本对象")
public class StoreDayDeptReport implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "大类编码", required = true)
	private String dept;
	@ApiModelProperty(value = "大类名称", required = true)
	private String deptName;
	@ApiModelProperty(value = "未税销售金额", required = true)
	private BigDecimal saleValue;
	@ApiModelProperty(value = "未税销售占比", required = true)
	private String saleRatio;
	@ApiModelProperty(value = "渗透率", required = true)
	private String permeationRatio;
	@ApiModelProperty(value = "含税销售金额", required = true)
	private BigDecimal saleValueIn;
	@ApiModelProperty(value = "含税毛利", required = true)
	private BigDecimal gpIn;
	@ApiModelProperty(value = "未税毛利", required = true)
	private BigDecimal gp;
	@ApiModelProperty(value = "毛利率", required = true)
	private String gpRatio;

}

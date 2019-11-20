package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "IncomeConfig", description = "后台收入率及DC收入率配置对象")
public class IncomeConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "门店ID", required = true)
	private String storeId;
	@ApiModelProperty(value = "大类ID", required = false)
	private String deptId;
	@ApiModelProperty(value = "后台收入率", required = false)
	private BigDecimal inValues;
	@ApiModelProperty(value = "DC收入率", required = false)
	private BigDecimal dcValues;
}

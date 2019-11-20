package com.cs.mobile.api.model.goods;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TodaySale", description = "当日销售对象")
public class TodaySale implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "当日含税销售金额", required = true)
	private BigDecimal amt;
	@ApiModelProperty(value = "当日未税销售金额", required = true)
	private BigDecimal amtEcl;
	@ApiModelProperty(value = "当日含税毛利额", required = true)
	private BigDecimal gp;
	@ApiModelProperty(value = "当日未税毛利额", required = true)
	private BigDecimal gpEcl;

}

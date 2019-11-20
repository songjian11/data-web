package com.cs.mobile.api.model.partner.runindex;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RunBaseReport", description = "经营指数基本对象")
public class RunBaseReport implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "时间", required = false)
	private String time;
	@ApiModelProperty(value = "数值", required = false)
	private BigDecimal value;

}

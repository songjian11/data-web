package com.cs.mobile.api.model.partner.dayreport;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * 时间段销售对象
 * 
 * @author wells
 * @date 2019年3月14日
 */
@Data
@ApiModel(value = "TimeSale", description = "时间段销售对象")
public class TimeSale implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "时间", required = true)
	private String time;
	@ApiModelProperty(value = "客流", required = true)
	private BigDecimal pfValue;
	@ApiModelProperty(value = "销售", required = true)
	private BigDecimal saleValue;
}

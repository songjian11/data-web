package com.cs.mobile.api.model.partner.assess.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 门店月度奖金明细
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "StoreBonusItemResp", description = "门店月度奖金明细对象")
public class StoreBonusItemResp implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月值", required = true)
	private String ym;
	@ApiModelProperty(value = "显示年月", required = true)
	private String showYm;
	@ApiModelProperty(value = "超额利润", required = true)
	private BigDecimal excessProfit;
	@ApiModelProperty(value = "奖金", required = true)
	private BigDecimal bonus;
}

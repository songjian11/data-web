package com.cs.mobile.api.model.partner.assess.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 门店奖金
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "StoreBonusResp", description = "门店奖金对象")
public class StoreBonusResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "月度参考奖金列表", required = true)
	private List<StoreBonusItemResp> storeBonusList;
	@ApiModelProperty(value = "季度名称", required = true)
	private String qName;
	@ApiModelProperty(value = "季度超额利润", required = true)
	private BigDecimal qExcessProfit;
	@ApiModelProperty(value = "季度奖金", required = true)
	private BigDecimal qbonus;
}

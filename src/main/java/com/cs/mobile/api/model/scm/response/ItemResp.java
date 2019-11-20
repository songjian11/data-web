package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ItemResp", description = "商品信息")
public class ItemResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "订货单位", required = true)
	private String uomDesc;
	@ApiModelProperty(value = "基地编码", required = true)
	private String supplier;
	@ApiModelProperty(value = "协议进价", required = true)
	private BigDecimal unitCost;
}

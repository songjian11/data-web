package com.cs.mobile.api.model.scm;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ItemTaxRate", description = "商品税率信息")
public class ItemTaxRate implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品税率名称", required = true)
	private BigDecimal vatRate;
}

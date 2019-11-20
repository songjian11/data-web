package com.cs.mobile.api.model.goods;

import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品销售及毛利数据
 * 
 * @author wells
 * @date 2019年3月6日
 */
@Data
@ApiModel(value = "GoodsSale", description = "商品销售及毛利数据")
public class GoodsSale implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "时间", required = true)
	private String time;
	@ApiModelProperty(value = "销售数量", required = true)
	private BigDecimal saleQty;
	@ApiModelProperty(value = "销售金额", required = true)
	private BigDecimal saleValue;
	@ApiModelProperty(value = "毛利额", required = true)
	private BigDecimal gp;
}

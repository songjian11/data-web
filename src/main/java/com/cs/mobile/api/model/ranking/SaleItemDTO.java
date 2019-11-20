package com.cs.mobile.api.model.ranking;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 大类-品类返回对象
 * 
 * @author jiangliang
 * @date 2019年4月01日
 */
@Data
@ApiModel(value = "SaleItemDTO", description = "单品返回对象")
public class SaleItemDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "大类ID", required = true)
	private String deptId;

	@ApiModelProperty(value = "大类名称", required = true)
	private String deptName;

	@ApiModelProperty(value = "单品ID", required = true)
	private String itemId;

	@ApiModelProperty(value = "单品名称", required = true)
	private String itemName;

	@ApiModelProperty(value = "单品销售金额", required = true)
	private BigDecimal saleValue;

	@ApiModelProperty(value = "单位", required = true)
	private String unit = "万";


	public BigDecimal getSaleValue() {
		return null == saleValue ? BigDecimal.ZERO : saleValue.divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
	}
}

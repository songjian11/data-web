package com.cs.mobile.api.model.scm.openapi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "DetailReq", description = "订单行信息请求参数对象")
public class DetailReq implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "重量单价（元/KG）", required = true)
	private BigDecimal unitPrice;
	@ApiModelProperty(value = "件数单价（元/件）", required = true)
	private BigDecimal perPrice;
	@ApiModelProperty(value = "订货单位", required = true)
	private String uomDesc;
	@ApiModelProperty(value = "数量", required = true)
	private BigDecimal poQty;
	@ApiModelProperty(value = "标准件", required = true)
	private BigDecimal standardOfPackage;
	@ApiModelProperty(value = "件数", required = true)
	private BigDecimal numberOfPackage;
	@ApiModelProperty(value = "备注", required = false)
	private String remark;
	@ApiModelProperty(value = "预测到仓价格（元/KG）", required = true)
	private BigDecimal predictArrivalPrice;

}

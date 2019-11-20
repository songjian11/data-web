package com.cs.mobile.api.model.scm.request;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PoDetailReq", description = "订单行信息请求参数对象")
public class PoDetailReq implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "订单号", required = true)
	private String poSn;
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
	@ApiModelProperty(value = "税率", required = true)
	private BigDecimal taxRate;
	@ApiModelProperty(value = "件数", required = true)
	private BigDecimal numberOfPackage;
	@ApiModelProperty(value = "备注", required = false)
	private String remark;

}

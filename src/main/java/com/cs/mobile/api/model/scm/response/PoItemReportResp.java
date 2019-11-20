package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PoItemReportResp", description = "基地回货单品报表")
public class PoItemReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "订货日期", required = true)
	private String orderDate;
	@ApiModelProperty(value = "发货日期", required = true)
	private String sendDate;
	@ApiModelProperty(value = "基地编码", required = true)
	private String supplier;
	@ApiModelProperty(value = "基地名称", required = true)
	private String supName;
	@ApiModelProperty(value = "订货价格", required = true)
	private String unitPrice;
	@ApiModelProperty(value = "发货价格", required = true)
	private String sendUnitPrice;
	@ApiModelProperty(value = "发货数量", required = true)
	private String sendQty;
	@ApiModelProperty(value = "发货金额", required = true)
	private String sendPrice;
}

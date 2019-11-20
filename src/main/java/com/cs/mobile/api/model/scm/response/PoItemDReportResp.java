package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PoItemDReportResp", description = "基地回货单品维度报表")
public class PoItemDReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "日期", required = true)
	private String sendDate;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "发货数量", required = true)
	private String sendQty;
	@ApiModelProperty(value = "发货金额", required = true)
	private String sendMoney;
	@ApiModelProperty(value = "车数", required = true)
	private String carCount;
	@ApiModelProperty(value = "订单单价", required = true)
	private String unitPrice;
	@ApiModelProperty(value = "到仓单价", required = true)
	private String sendPrice;
}

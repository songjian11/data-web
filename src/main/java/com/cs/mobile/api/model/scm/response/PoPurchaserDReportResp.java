package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PoPurchaserDReportResp", description = "基地回货采购员维度报表")
public class PoPurchaserDReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "日期", required = true)
	private String sendDate;
	@ApiModelProperty(value = "采购员", required = true)
	private String purchaser;
	@ApiModelProperty(value = "车型", required = true)
	private String carType;
	@ApiModelProperty(value = "发货数量", required = true)
	private String sendQty;
	@ApiModelProperty(value = "发货金额", required = true)
	private String sendMoney;
	@ApiModelProperty(value = "sku数量", required = true)
	private String skuCount;
	@ApiModelProperty(value = "车数", required = true)
	private String carCount;
}

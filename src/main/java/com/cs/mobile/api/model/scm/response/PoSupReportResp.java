package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PoSupReportResp", description = "基地回基地报表")
public class PoSupReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "基地编码", required = true)
	private String supplier;
	@ApiModelProperty(value = "基地名称", required = true)
	private String supName;
	@ApiModelProperty(value = "税率", required = true)
	private String taxRate;
	@ApiModelProperty(value = "数量", required = true)
	private String sendQty;
	@ApiModelProperty(value = "含税金额", required = true)
	private String sendPrice;
	@ApiModelProperty(value = "未税金额", required = true)
	private String noTaxPrice;
	@ApiModelProperty(value = "税金", required = true)
	private String taxMoney;
}

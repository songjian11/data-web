package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PoSupplierDReportResp", description = "基地回货供应商维度报表")
public class PoSupplierDReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "基地编码", required = true)
	private String supplier;
	@ApiModelProperty(value = "基地名称", required = true)
	private String supName;
	@ApiModelProperty(value = "发货数量", required = true)
	private String sendQty;
	@ApiModelProperty(value = "发货金额", required = true)
	private String sendMoney;

}

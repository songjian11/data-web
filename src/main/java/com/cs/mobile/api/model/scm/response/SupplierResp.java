package com.cs.mobile.api.model.scm.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SupplierResp", description = "基地信息")
public class SupplierResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "基地编码", required = true)
	private String supplier;
	@ApiModelProperty(value = "基地名称", required = true)
	private String supName;
}

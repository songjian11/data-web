package com.cs.mobile.api.model.common;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "组织架构信息", description = "组织架构信息")
public class Organization implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "组织ID", required = false)
	private String orgId;
	@ApiModelProperty(value = "组织名称", required = false)
	private String orgName;
}

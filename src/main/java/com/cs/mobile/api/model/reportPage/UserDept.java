package com.cs.mobile.api.model.reportPage;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserDept", description = "用户报表大类范围")
public class UserDept implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "员工工号", required = true)
	private String personId;
	@ApiModelProperty(value = "大类id", required = true)
	private Integer deptId;
	@ApiModelProperty(value = "大类名称", required = true)
	private String deptName;
	@ApiModelProperty(value = "品类", required = true)
	private String category;
	@ApiModelProperty(value = "总品类", required = true)
	private String pCategory;

}

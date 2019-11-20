package com.cs.mobile.api.model.user;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统用户持久类
 * 
 * @author wells.wong
 * @date 2018年11月19日
 */
@Data
@ApiModel(value = "系统用户", description = "用户对象")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "工号", required = true)
	private String personId;
	@ApiModelProperty(value = "姓名", required = true)
	private String name;
	@ApiModelProperty(value = "类型", required = true)
	private Integer type;
	@ApiModelProperty(value = "机构", required = true)
	private Integer orgId;
	@ApiModelProperty(value = "状态", required = false)
	private Integer status;
}

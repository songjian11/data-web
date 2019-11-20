package com.cs.mobile.api.model.common;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 大类对象
 * 
 * @author wells
 * @date 2019年3月1日
 */
@Data
@ApiModel(value = "DeptInfo", description = "大类对象")
public class DeptInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "门店ID", required = true)
	private String storeId;
	@ApiModelProperty(value = "小店ID", required = true)
	private String comId;
	@ApiModelProperty(value = "大类ID", required = true)
	private String deptId;
}

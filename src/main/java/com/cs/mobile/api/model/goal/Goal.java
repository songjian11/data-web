package com.cs.mobile.api.model.goal;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Goal", description = "目标对象")
public class Goal extends GoalValue implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = false)
	private String goalYm;
	@ApiModelProperty(value = "省份编码", required = false)
	private String provinceId;
	@ApiModelProperty(value = "区域编码", required = false)
	private String areaId;
	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店编码", required = false)
	private String comId;
	@ApiModelProperty(value = "大类编码", required = false)
	private String deptId;
}

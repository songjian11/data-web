package com.cs.mobile.api.model.goal;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GoalValue", description = "目标值对象")
public class GoalValue implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "门店ID", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店ID", required = false)
	private String comId;
	@ApiModelProperty(value = "科目", required = false)
	private String subject;
	@ApiModelProperty(value = "数值", required = false)
	private BigDecimal subValues;

	public String getSerialNo() {
		return this.storeId + "_" + this.comId;
	}
}

package com.cs.mobile.api.model.partner.battle;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BaseReport", description = "基本报表对象")
public class BaseReport implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "时间", required = false)
	private String time;
	@ApiModelProperty(value = "数值", required = false)
	private BigDecimal value;
	@ApiModelProperty(value = "组织ID", required = false)
	private String orgId;
	@ApiModelProperty(value = "组织名称", required = false)
	private String orgName;
	@ApiModelProperty(value = "门店ID", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店ID", required = false)
	private String comId;
	@ApiModelProperty(value = "门店可比标识", required = false)
	private Integer isCompare;
}

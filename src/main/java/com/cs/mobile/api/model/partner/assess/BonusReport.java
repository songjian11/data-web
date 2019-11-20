package com.cs.mobile.api.model.partner.assess;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BonusReport", description = "奖金报表对象")
public class BonusReport implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "省份ID", required = true)
	private String provinceId;
	@ApiModelProperty(value = "省份名称", required = true)
	private String provinceName;
	@ApiModelProperty(value = "区域ID", required = true)
	private String areaId;
	@ApiModelProperty(value = "区域省份名称", required = true)
	private String areaName;
	@ApiModelProperty(value = "门店ID", required = true)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = true)
	private String storeName;
	@ApiModelProperty(value = "小店ID", required = true)
	private String comId;
	@ApiModelProperty(value = "小店名称", required = true)
	private String comName;
	@ApiModelProperty(value = "奖金", required = true)
	private BigDecimal bonus;

}

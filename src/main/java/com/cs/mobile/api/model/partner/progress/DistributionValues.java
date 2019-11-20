package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分配比例表
 * 
 * @author wells
 * @time 2018年12月18日
 */
@Data
@ApiModel(value = "DistributionValues", description = "分配比例表对象")
public class DistributionValues implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "门店ID", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店ID", required = false)
	private String comId;
	@ApiModelProperty(value = "比例值", required = false)
	private BigDecimal value;

}

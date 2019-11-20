package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标准周转天数
 * 
 * @author wells
 * @time 2018年12月18日
 */
@Data
@ApiModel(value = "TurnOverDays", description = "标准周转天数对象")
public class TurnOverDays implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = false)
	private String ym;
	@ApiModelProperty(value = "门店ID", required = false)
	private String storeId;
	@ApiModelProperty(value = "大类ID", required = false)
	private String deptId;
	@ApiModelProperty(value = "周转天数", required = false)
	private Integer days;
}

package com.cs.mobile.api.model.common;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CalendarMapping", description = "日期映射")
public class CalendarMapping implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "当前日期", required = true)
	private String aDay;
	@ApiModelProperty(value = "去年对应日期", required = true)
	private String bDay;

}

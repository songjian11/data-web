package com.cs.mobile.api.model.mreport;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AreaGroupReport", description = "大区报表基本对象")
public class AreaGroupReport implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "大战区", required = true)
	private String areaGroupName;
	@ApiModelProperty(value = "区域", required = true)
	private String areaName;
	@ApiModelProperty(value = "昨日前台毛利率", required = true)
	private String yesterdayFrontGpRatio;
	@ApiModelProperty(value = "去年昨日前台毛利率", required = true)
	private String lastYearDayFrontGpRatio;
	@ApiModelProperty(value = "日毛利率增长", required = true)
	private String dayGpRise;
	@ApiModelProperty(value = "月至今前台毛利率", required = true)
	private String frontGpRatio;
	@ApiModelProperty(value = "去年月至今前台毛利率", required = true)
	private String lastYearFrontGpRatio;
	@ApiModelProperty(value = "月累计毛利率增长", required = true)
	private String monthGpRise;

}

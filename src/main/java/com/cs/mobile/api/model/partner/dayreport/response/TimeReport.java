package com.cs.mobile.api.model.partner.dayreport.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 当日时间段比较数据对象
 * 
 * @author wells
 * @date 2019年3月13日
 */
@Data
@ApiModel(value = "TimeReport", description = "当日时间段比较数据对象")
public class TimeReport implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "时段", required = true)
	private String time;
	@ApiModelProperty(value = "客流", required = true)
	private CompareData passengerFlow;
	@ApiModelProperty(value = "销售", required = true)
	private CompareData sale;
	@ApiModelProperty(value = "客单", required = true)
	private CompareData perPrice;

	public int getCompare() {
		return Integer.parseInt(this.time);
	}
}

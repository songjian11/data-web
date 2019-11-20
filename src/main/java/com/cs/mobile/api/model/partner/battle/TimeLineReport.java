package com.cs.mobile.api.model.partner.battle;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TimeLineReport", description = "时间轴报表对象")
public class TimeLineReport implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "真实时间", required = false)
	private String time;
	@ApiModelProperty(value = "显示时间", required = false)
	private String showTime;
	@ApiModelProperty(value = "数值", required = false)
	private BigDecimal value;
	@ApiModelProperty(value = "单位", required = false)
	private String unit;

	public int getCompare() {
		return Integer.parseInt(this.time.replace("-", ""));
	}

	public String getShowTime() {
		return this.time.substring(time.length() - 2, time.length());
	}

}

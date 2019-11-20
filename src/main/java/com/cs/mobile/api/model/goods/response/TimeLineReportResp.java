package com.cs.mobile.api.model.goods.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品销售及毛利时间轴对象
 * 
 * @author wells
 * @date 2019年3月6日
 */
@Data
@ApiModel(value = "TimeLineReportResp", description = "时间轴报表对象")
public class TimeLineReportResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "真实时间", required = true)
	private String time;
	@ApiModelProperty(value = "显示时间", required = true)
	private String showTime;
	@ApiModelProperty(value = "数值", required = true)
	private BigDecimal value;
	@ApiModelProperty(value = "单位", required = true)
	private String unit;

}

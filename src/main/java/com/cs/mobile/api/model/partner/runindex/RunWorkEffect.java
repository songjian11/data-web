package com.cs.mobile.api.model.partner.runindex;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RunWorkEffect", description = "劳效趋势数据")
public class RunWorkEffect implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = false)
	private String time;
	@ApiModelProperty(value = "月份", required = false)
	private String month;
	@ApiModelProperty(value = "数值", required = false)
	private BigDecimal value;
	@ApiModelProperty(value = "比率", required = false)
	private BigDecimal ratio;

	public String getMonth() {
		return this.time.substring(5, 7) + "月";
	}
	
	public int getCompare() {
		return Integer.parseInt(this.time.replace("-", ""));
	}

}

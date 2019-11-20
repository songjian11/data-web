package com.cs.mobile.api.model.partner.battle;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.common.utils.OperationUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RankReport", description = "排名报表对象")
public class RankReport implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "编码", required = false)
	private String code;
	@ApiModelProperty(value = "名称", required = false)
	private String name;
	@ApiModelProperty(value = "目标值", required = false)
	private BigDecimal targetValue;
	@ApiModelProperty(value = "进度值", required = false)
	private BigDecimal processValue;
	@ApiModelProperty(value = "单位", required = false)
	private String unit;
	@ApiModelProperty(value = "比率=进度值/目标值", required = false)
	private BigDecimal processRatio;
	@ApiModelProperty(value = "同期值", required = false)
	private BigDecimal compareValue;
	@ApiModelProperty(value = "增长率", required = false)
	private BigDecimal riseRatio;

	public BigDecimal getProcessRatio() {
		return OperationUtil.divideHandler(processValue, targetValue).multiply(new BigDecimal(100));
	}

	public BigDecimal getRiseRatio() {
		return OperationUtil.divideHandler(processValue.subtract(compareValue), compareValue)
				.multiply(new BigDecimal(100));
	}
}

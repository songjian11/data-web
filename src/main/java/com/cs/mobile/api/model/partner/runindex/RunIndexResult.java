package com.cs.mobile.api.model.partner.runindex;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RunReportResult", description = "经营指数对象")
public class RunIndexResult implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "档位", required = false)
	private String levelName;
	@ApiModelProperty(value = "劳效达成率", required = false)
	private String levelRatio;
	@ApiModelProperty(value = "劳效总额", required = false)
	private BigDecimal workEffectTotal;
	@ApiModelProperty(value = "劳效目标额", required = false)
	private BigDecimal workEffectTarget;
	@ApiModelProperty(value = "店内设备数量", required = false)
	private Integer machineTotal;
	@ApiModelProperty(value = "设备单位", required = false)
	private String machineUnit;
	@ApiModelProperty(value = "店内设备电费", required = false)
	private Integer eChargeTotal;
	@ApiModelProperty(value = "电费单位", required = false)
	private String eChargeUnit;
	@ApiModelProperty(value = "店内面积", required = false)
	private BigDecimal areaTotal;
	@ApiModelProperty(value = "面积单位", required = false)
	private String areaUnit;
	@ApiModelProperty(value = "店内人员", required = false)
	private BigDecimal personTotal;
	@ApiModelProperty(value = "人员单位", required = false)
	private String personUnit;

}

package com.cs.mobile.api.model.partner.runindex;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 劳效档位配置表
 * 
 * @author wells.wong
 * @date 2018年11月24日
 */
@Data
@ApiModel(value = "WorkLevelConfig", description = "劳效档位配置对象")
public class WorkLevelConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "店群ID", required = false)
	private Integer groupId;
	@ApiModelProperty(value = "最大达成率", required = false)
	private BigDecimal maxCompletRate;
	@ApiModelProperty(value = "最小达成率", required = false)
	private BigDecimal minCompletRate;
	@ApiModelProperty(value = "达成档", required = false)
	private String levelName;
	@ApiModelProperty(value = "岗位", required = false)
	private String positionName;
	@ApiModelProperty(value = "奖金", required = false)
	private BigDecimal bonus;
}

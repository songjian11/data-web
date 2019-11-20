package com.cs.mobile.api.model.partner.ranking;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ScoreItem", description = "得分明细对象")
public class ScoreItem implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "得分项", required = false)
	private String item;
	@ApiModelProperty(value = "得分", required = false)
	private BigDecimal score;
	@ApiModelProperty(value = "得分标准", required = false)
	private BigDecimal scoreStandard;
}

package com.cs.mobile.api.model.partner.ranking;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RankingScore", description = "赛马积分明细表对象")
public class RankingScore implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = false)
	private String ym;
	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店编码", required = false)
	private String comId;
	@ApiModelProperty(value = "指标编码", required = false)
	private String typeCode;
	@ApiModelProperty(value = "条件编码", required = false)
	private String conditionCode;
	@ApiModelProperty(value = "得分", required = false)
	private BigDecimal score;
}

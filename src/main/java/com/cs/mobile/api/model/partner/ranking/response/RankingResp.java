package com.cs.mobile.api.model.partner.ranking.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RankingResp", description = "赛马排名结果对象")
public class RankingResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "编码", required = false)
	private String code;
	@ApiModelProperty(value = "名称", required = false)
	private String name;
	@ApiModelProperty(value = "总得分", required = false)
	private BigDecimal scoreTotal;
	@ApiModelProperty(value = "销售额", required = false)
	private BigDecimal saleTotal;
	@ApiModelProperty(value = "利润额", required = false)
	private BigDecimal profitTotal;
	@ApiModelProperty(value = "排名", required = false)
	private Integer rank;
}

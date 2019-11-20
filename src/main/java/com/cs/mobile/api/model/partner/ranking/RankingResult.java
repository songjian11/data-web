package com.cs.mobile.api.model.partner.ranking;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RankingResult", description = "赛马排名结果表对象")
public class RankingResult implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = false)
	private String ym;
	@ApiModelProperty(value = "店群ID", required = false)
	private String groupId;
	@ApiModelProperty(value = "类型编码", required = false)
	private String type;
	@ApiModelProperty(value = "类型名称", required = false)
	private String typeName;
	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店编码", required = false)
	private String comId;
	@ApiModelProperty(value = "总得分", required = false)
	private BigDecimal scoreTotal;
	@ApiModelProperty(value = "销售额", required = false)
	private BigDecimal saleTotal;
	@ApiModelProperty(value = "利润额", required = false)
	private BigDecimal profitTotal;
	@ApiModelProperty(value = "排名", required = false)
	private Integer rank;
}

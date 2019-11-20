package com.cs.mobile.api.model.partner.ranking.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.cs.mobile.api.model.partner.ranking.ScoreItem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RankingDetailResp", description = "排行详情对象")
public class RankingDetailResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = false)
	private String storeName;
	@ApiModelProperty(value = "排名", required = false)
	private Integer rank;
	@ApiModelProperty(value = "总得分", required = false)
	private BigDecimal scoreTotal;
	@ApiModelProperty(value = "销售额", required = false)
	private BigDecimal saleTotal;
	@ApiModelProperty(value = "利润额", required = false)
	private BigDecimal profitTotal;
	@ApiModelProperty(value = "跟上月比较情况", required = false)
	private String diffDesc;
	@ApiModelProperty(value = "得分项", required = false)
	private List<ScoreItem> scoreItemList;

}

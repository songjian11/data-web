package com.cs.mobile.api.model.partner.ranking.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.cs.mobile.api.model.partner.ranking.ScoreItem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserRankResp", description = "个人所在（小店/大店）排名对象")
public class UserRankingResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "上月排名", required = false)
	private Integer rank;
	@ApiModelProperty(value = "上月得分", required = false)
	private BigDecimal score;
	@ApiModelProperty(value = "店群名", required = false)
	private String groupName;
	@ApiModelProperty(value = "店群团队数量", required = false)
	private Integer groupCount;
	@ApiModelProperty(value = "得分明细", required = false)
	List<ScoreItem> scoreItemList;
}

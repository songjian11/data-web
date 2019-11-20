package com.cs.mobile.api.model.partner.ranking.response;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserMonthRankingResp", description = "个人所在（小店/大店）月排名对象")
public class UserRankingTimeLineResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "真实时间", required = false)
	private String time;
	@ApiModelProperty(value = "显示时间", required = false)
	private String showTime;
	@ApiModelProperty(value = "总得分", required = false)
	private BigDecimal scoreTotal;
	@ApiModelProperty(value = "排名", required = false)
	private Integer rank;
}

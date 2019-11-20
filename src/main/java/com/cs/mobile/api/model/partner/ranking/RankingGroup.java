package com.cs.mobile.api.model.partner.ranking;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "RankingScore", description = "赛马店群表对象")
public class RankingGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "店群ID", required = false)
	private String groupId;
	@ApiModelProperty(value = "类型编码", required = false)
	private String type;
	@ApiModelProperty(value = "类型名称", required = false)
	private String typeName;
	@ApiModelProperty(value = "小店编码", required = false)
	private String comId;
}

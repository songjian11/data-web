package com.cs.mobile.api.model.towerteam.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TowerTeamAreaResp", description = "区域对象")
public class TowerTeamAreaResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "编码", required = false)
	private String code;
	@ApiModelProperty(value = "名称", required = false)
	private String name;
}

package com.cs.mobile.api.model.towerteam.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TowerTeamPositionResp", description = "支援门店岗位对象")
public class TowerTeamPositionResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "岗位编码", required = false)
	private String positionId;
	@ApiModelProperty(value = "岗位名称", required = false)
	private String positionName;
	@ApiModelProperty(value = "需要报名总数", required = false)
	private Integer personTotal;
	@ApiModelProperty(value = "可用报名总数", required = false)
	private Integer available;
	@ApiModelProperty(value = "当前已报名总数", required = false)
	private Integer curPersonTotal;

	public Integer getCurPersonTotal() {
		return this.personTotal - this.available;
	}
}

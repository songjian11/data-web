package com.cs.mobile.api.model.towerteam.response;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TowerTeamStoreResp", description = "支援门店对象")
public class TowerTeamStoreResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = false)
	private String storeName;
	@ApiModelProperty(value = "岗位数组", required = false)
	private List<TowerTeamPositionResp> positionResp;

}

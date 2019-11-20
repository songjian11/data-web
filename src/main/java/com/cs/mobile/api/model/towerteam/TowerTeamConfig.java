package com.cs.mobile.api.model.towerteam;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TowerTeamConfig", description = "支援门店配置对象")
public class TowerTeamConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "省份编码", required = false)
	private String provinceId;
	@ApiModelProperty(value = "省份名称", required = false)
	private String provinceName;
	@ApiModelProperty(value = "大区编码", required = false)
	private String areaGroupId;
	@ApiModelProperty(value = "大区名称", required = false)
	private String areaGroupName;
	@ApiModelProperty(value = "区域编码", required = false)
	private String areaId;
	@ApiModelProperty(value = "区域名称", required = false)
	private String areaName;
	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = false)
	private String storeName;
	@ApiModelProperty(value = "岗位编码", required = false)
	private String positionId;
	@ApiModelProperty(value = "岗位名称", required = false)
	private String positionName;
	@ApiModelProperty(value = "可报名数", required = false)
	private Integer personTotal;
	@ApiModelProperty(value = "剩余数量", required = false)
	private Integer available;
}

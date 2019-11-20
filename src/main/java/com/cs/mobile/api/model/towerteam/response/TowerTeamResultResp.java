package com.cs.mobile.api.model.towerteam.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TowerTeamResultResp", description = "支援门店报名结果对象")
public class TowerTeamResultResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "已报名的省份", required = false)
	private String provinceName;
	@ApiModelProperty(value = "已报名的大区", required = false)
	private String areaGroupName;
	@ApiModelProperty(value = "已报名的区域", required = false)
	private String areaName;
	@ApiModelProperty(value = "已报名的门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "已报名的门店", required = false)
	private String storeName;
	@ApiModelProperty(value = "已报名的岗位编码", required = false)
	private String positionId;
	@ApiModelProperty(value = "已报名的岗位", required = false)
	private String positionName;
	@ApiModelProperty(value = "报名的时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private String createTime;
}

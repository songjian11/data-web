package com.cs.mobile.api.model.towerteam.request;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TowerTeamSubmitReq", description = "支援门店提交请求参数对象")
public class TowerTeamSubmitReq implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "工号", required = false)
	private String personId;
	@ApiModelProperty(value = "姓名", required = false)
	private String name;
	@ApiModelProperty(value = "电话", required = false)
	private String phone;
	@ApiModelProperty(value = "报名门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "性别", required = false)
	private String gender;
	@ApiModelProperty(value = "报名岗位编码", required = false)
	private String positionId;
}

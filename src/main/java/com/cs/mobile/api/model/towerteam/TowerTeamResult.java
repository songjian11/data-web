package com.cs.mobile.api.model.towerteam;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TowerTeamResult", description = "支援门店报名结果对象")
public class TowerTeamResult implements Serializable {
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
	@ApiModelProperty(value = "状态", required = false)
	private Integer status;
	@ApiModelProperty(value = "报名时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Date createTime;
	@ApiModelProperty(value = "更新时间", required = false)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Date updateTime;
}

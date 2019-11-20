package com.cs.mobile.api.model.partner;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 合伙人用户信息持久类
 * 
 * @author wells.wong
 * @date 2018年11月19日
 */
@Data
@ApiModel(value = "合伙人用户信息", description = "合伙人用户用户信息对象")
public class PartnerUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "公司编码", required = false)
	private String enterpriseId;
	@ApiModelProperty(value = "公司名称", required = false)
	private String enterpriseName;
	@ApiModelProperty(value = "省份编码", required = false)
	private String provinceId;
	@ApiModelProperty(value = "省份名称", required = false)
	private String provinceName;
	@ApiModelProperty(value = "区域编码", required = false)
	private String areaId;
	@ApiModelProperty(value = "区域名称", required = false)
	private String areaName;
	@ApiModelProperty(value = "店群编码", required = false)
	private String groupId;
	@ApiModelProperty(value = "店群名称", required = false)
	private String groupName;
	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = false)
	private String storeName;
	@ApiModelProperty(value = "小店编码", required = false)
	private String comId;
	@ApiModelProperty(value = "小店名称", required = false)
	private String comName;
	@ApiModelProperty(value = "所在门店可比标识", required = false)
	private Integer isCompare;
}

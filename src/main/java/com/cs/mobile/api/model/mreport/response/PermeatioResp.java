package com.cs.mobile.api.model.mreport.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PermeatioResp", description = "渗透率对象")
public class PermeatioResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "省份", required = true)
	private String provinceName;
	@ApiModelProperty(value = "区域", required = true)
	private String areaName;
	@ApiModelProperty(value = "门店编码", required = true)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = true)
	private String storeName;
	@ApiModelProperty(value = "当日整体客流", required = true)
	private String dayAllKl;
	@ApiModelProperty(value = "当日扫码购客流", required = true)
	private String dayScanKl;
	@ApiModelProperty(value = "当日微信自助收银客流", required = true)
	private String dayWxKl;
	@ApiModelProperty(value = "当日扫码购渗透率", required = true)
	private String dayScanPermeation;
	@ApiModelProperty(value = "当日微信自助渗透率", required = true)
	private String dayWxPermeation;
	@ApiModelProperty(value = "当日智慧收银渗透率", required = true)
	private String daySmartPermeation;
	@ApiModelProperty(value = "月累计整体客流", required = true)
	private String monthAllKl;
	@ApiModelProperty(value = "月累计扫码购客流", required = true)
	private String monthScanKl;
	@ApiModelProperty(value = "月累微信自助收银客流", required = true)
	private String monthWxKl;
	@ApiModelProperty(value = "月累计扫码购渗透率", required = true)
	private String monthScanPermeation;
	@ApiModelProperty(value = "月累计微信自助渗透率", required = true)
	private String monthWxPermeation;
	@ApiModelProperty(value = "月累计智慧收银渗透率", required = true)
	private String monthSmartPermeation;
}

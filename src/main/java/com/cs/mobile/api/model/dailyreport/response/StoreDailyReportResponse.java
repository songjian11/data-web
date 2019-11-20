package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "StoreDailyReportResponse", description = "门店日报表")
public class StoreDailyReportResponse extends DailyReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "省份名称", required = true)
    private String provinceName;
    @ApiModelProperty(value = "省份ID", required = true)
    private String provinceId;
    @ApiModelProperty(value = "区域名称", required = true)
    private String areaName;
    @ApiModelProperty(value = "区域ID", required = true)
    private String areaId;
    @ApiModelProperty(value = "门店编码", required = true)
    private String storeId;
    @ApiModelProperty(value = "门店名称", required = true)
    private String storeName;
    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    @ApiModelProperty(value = "大类编码", required = true)
    private String deptId;
}

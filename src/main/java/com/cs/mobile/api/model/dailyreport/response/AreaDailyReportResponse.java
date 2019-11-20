package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "AreaDailyReportResponse", description = "区域日报表")
public class AreaDailyReportResponse extends DailyReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //省份名称
    @ApiModelProperty(value = "省份名称", required = true)
    private String provinceName;
    @ApiModelProperty(value = "省份ID", required = true)
    private String provinceId;

    //区域名称
    @ApiModelProperty(value = "区域名称", required = true)
    private String areaName;
    @ApiModelProperty(value = "区域ID", required = true)
    private String areaId;

    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    @ApiModelProperty(value = "大类编号", required = true)
    private String deptId;
}

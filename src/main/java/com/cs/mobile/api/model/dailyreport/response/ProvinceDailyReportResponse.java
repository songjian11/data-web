package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel(value = "ProvinceDailyReportResponse", description = "省份日报表")
public class ProvinceDailyReportResponse extends DailyReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "省份名称", required = true)
    private String provinceName;

    @ApiModelProperty(value = "省份ID", required = true)
    private String provinceId;

    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;

    @ApiModelProperty(value = "大类编号", required = true)
    private String deptId;
}

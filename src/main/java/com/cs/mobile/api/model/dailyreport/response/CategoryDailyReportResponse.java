package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "CategoryDailyReportResponse", description = "大类汇总日报表")
public class CategoryDailyReportResponse extends DailyReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "品类名称", required = true)
    private String categoryName;

    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;

    @ApiModelProperty(value = "大类编号", required = true)
    private String deptId;
}

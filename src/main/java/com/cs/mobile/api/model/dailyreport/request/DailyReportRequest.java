package com.cs.mobile.api.model.dailyreport.request;

import com.cs.mobile.api.model.reportPage.ReportCommonParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "DailyReportRequest", description = "日报表基本参数")
public class DailyReportRequest extends ReportCommonParam implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "全司", required = false)
    private String enterpriseId;
    @ApiModelProperty(value = "查询梯度,初始化时传默认值0,下钻时取每行梯度值加1(0-默认值，1-全司，2-省份，3-区域,4-门店)", required = true)
    private int mark = 0;
    @ApiModelProperty(value = "是否可比,默认查询全部(0-全部，1-可比，2-全比)", required = true)
    private int key = 0;
    @ApiModelProperty(value = "页数", required = true)
    private int page;
    @ApiModelProperty(value = "每页记录数", required = true)
    private int pageSize;
    //用户权限等级
    @JsonIgnore
    private int grade;
}

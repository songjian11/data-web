package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "FreshDailyReportListResponse", description = "生鲜战区报表列表")
public class FreshDailyReportListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "省份日报表", required = true)
    private List<FreshDailyReportResponse> provinceDayList;
    @ApiModelProperty(value = "省份月报表", required = true)
    private List<FreshDailyReportResponse> provinceMonthList;

    @ApiModelProperty(value = "战区日报表", required = true)
    private List<FreshDailyReportResponse> theaterDayList;
    @ApiModelProperty(value = "战区月报表", required = true)
    private List<FreshDailyReportResponse> theaterMonthList;

    @ApiModelProperty(value = "区域日报表", required = true)
    private List<FreshDailyReportResponse> areaDayList;
    @ApiModelProperty(value = "区域月报表", required = true)
    private List<FreshDailyReportResponse> areaMonthList;

    @ApiModelProperty(value = "区域日趋势", required = true)
    private List<FreshDailyReportResponse> areaDayTrendList;
    @ApiModelProperty(value = "区域月趋势", required = true)
    private List<FreshDailyReportResponse> areaMonthTrendList;
}

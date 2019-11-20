package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
@ApiModel(value = "StoreFreshDailyReportListResponse", description = "生鲜门店战区报表列表")
public class StoreFreshDailyReportListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "区域战区报表", required = true)
    private List<FreshDailyReportResponse> areaList;
    @ApiModelProperty(value = "门店战区报表", required = true)
    private List<StoreFreshDailyReportResponse> storeList;
    @ApiModelProperty(value = "门店战区日趋势", required = true)
    private List<StoreFreshDailyReportResponse> storeDayTrendList;
    @ApiModelProperty(value = "门店战区月趋势", required = true)
    private List<StoreFreshDailyReportResponse> storeMonthTrendList;
}

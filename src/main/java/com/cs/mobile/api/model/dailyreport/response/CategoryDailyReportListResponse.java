package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "CategoryDailyReportResponse", description = "品类日报表")
public class CategoryDailyReportListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "全司级品类列表", required = true)
    private List<CategoryDailyReportResponse> enterpriseList;
    
    @ApiModelProperty(value = "省份级品类列表", required = true)
    private List<ProvinceDailyReportResponse> provinceList;
    
    @ApiModelProperty(value = "区域级品类列表", required = true)
    private List<AreaDailyReportResponse> areaList;
    
    @ApiModelProperty(value = "门店级品类列表", required = true)
    private List<StoreDailyReportResponse> storeList;
    
    
    
    
}

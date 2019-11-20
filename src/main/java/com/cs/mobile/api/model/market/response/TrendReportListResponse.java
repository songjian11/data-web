package com.cs.mobile.api.model.market.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
@ApiModel(value = "TrendReportListResponse", description = "趋势列")
public class TrendReportListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "趋势列",required = true)
    private List<TrendReportResponse> list;

    public void setTaxData() throws IllegalAccessException {
        if(null != list && list.size() > 0){
            for(TrendReportResponse trendReportResponse : list){
                trendReportResponse.setTaxData();
            }
        }
    }
}

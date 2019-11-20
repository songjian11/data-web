package com.cs.mobile.api.model.freshspecialreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "freshSpecialReportTrendListResponse", description = "销售趋势列表")
public class FreshSpecialReportTrendListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "销售趋势明细",required = true)
    private List<FreshSpecialReportTrendResponse> list;
    @ApiModelProperty(value = "用户权限等级(1-全司，2-省份，3-区域，4-门店)",required = true)
    private int grade;

    public void setTaxData() throws IllegalAccessException {
        if(null != this.list && this.list.size() > 0){
            for(FreshSpecialReportTrendResponse freshSpecialReportTrendResponse : list){
                freshSpecialReportTrendResponse.setTaxData();
            }
        }
    }
}

package com.cs.mobile.api.model.reportPage.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

@Data
@ApiModel(value = "ReportDataResponse", description = "时线，日线，月线")
public class ReportDataResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "当期", required = true)
    private List<ReportDetailResponse> current;
    @ApiModelProperty(value = "同期", required = true)
    private List<ReportDetailResponse> same;

    public void setTaxData() throws IllegalAccessException {
        if(null != this.current && this.current.size() > 0){
            for(ReportDetailResponse reportDetailResponse : this.current){
                reportDetailResponse.setTaxData();
            }
        }

        if(null != this.same && this.same.size() > 0){
            for(ReportDetailResponse reportDetailResponse : this.same){
                reportDetailResponse.setTaxData();
            }
        }
    }
}

package com.cs.mobile.api.model.salereport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

@Data
@ApiModel(value = "SaleTrendListResponse", description = "销售趋势列表")
public class SaleTrendListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "销售趋势列表", required = true)
    private List<SaleTrendResponse> list;

    public void setTaxData() throws IllegalAccessException {
        if(null != this.list && this.list.size() > 0){
            for(SaleTrendResponse saleTrendResponse : this.list){
                saleTrendResponse.setTaxData();
            }
        }
    }
}

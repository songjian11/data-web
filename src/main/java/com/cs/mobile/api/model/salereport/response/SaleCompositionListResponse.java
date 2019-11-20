package com.cs.mobile.api.model.salereport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "SaleCompositionListResponse", description = "销售构成列")
public class SaleCompositionListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "销售构成行", required = true)
    private List<SaleCompositionResponse> list;

    public void setTaxData() throws IllegalAccessException {
        if(null != this.list && this.list.size() > 0){
            for(SaleCompositionResponse saleCompositionResponse : this.list){
                saleCompositionResponse.setTaxData();
            }
        }
    }
}

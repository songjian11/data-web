package com.cs.mobile.api.model.market.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "DepartmentSaleListResponse", description = "市场报表列")
public class DepartmentSaleListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "部门汇总",required = true)
    private List<DepartmentSaleResponse> list;

    public void setTaxData() throws IllegalAccessException {
        if(null != list && list.size() > 0){
            for(DepartmentSaleResponse departmentSaleResponse : list){
                departmentSaleResponse.setTaxData();
            }
        }
    }
}

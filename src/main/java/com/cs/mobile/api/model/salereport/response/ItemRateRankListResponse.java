package com.cs.mobile.api.model.salereport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "ItemRateRankListResponse", description = "大类单品毛利额排名列表")
public class ItemRateRankListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "毛利额单品排行列表", required = true)
    private List<ItemRateRankResponse> items;

    @ApiModelProperty(value = "大类列表", required = true)
    private List<String> depts;

    @ApiModelProperty(value = "单位", required = true)
    private String unit ="元";

    public void setTaxData() throws IllegalAccessException {
        if(null != this.items && this.items.size() > 0){
            for(ItemRateRankResponse itemRateRankResponse : this.items){
                itemRateRankResponse.setTaxData();
            }
        }
    }
}

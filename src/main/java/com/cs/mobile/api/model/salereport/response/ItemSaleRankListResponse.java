package com.cs.mobile.api.model.salereport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ApiModel(value = "ItemSaleRankListResponse", description = "大类单品销售排名列表")
public class ItemSaleRankListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "销售单品排行列表", required = true)
    private List<ItemSaleRankResponse> items;

    @ApiModelProperty(value = "大类列表", required = true)
    private List<String> depts;

    @ApiModelProperty(value = "单位", required = true)
    private String unit ="元";

    public void setTaxData() throws IllegalAccessException {
        if(null != this.items && this.items.size() > 0){
            for(ItemSaleRankResponse itemSaleRankResponse : this.items){
                itemSaleRankResponse.setTaxData();
            }
        }
    }
}

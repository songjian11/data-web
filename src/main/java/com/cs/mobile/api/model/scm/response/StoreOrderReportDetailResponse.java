package com.cs.mobile.api.model.scm.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreOrderReportDetailResponse", description = "门店要货报表详情")
public class StoreOrderReportDetailResponse {
    @ApiModelProperty(value = "门店编号", required = true)
    private String storeId;
    @ApiModelProperty(value = "门店名称", required = true)
    private String storeName;
    @ApiModelProperty(value = "下单数量", required = true)
    private String orderNum;
    @ApiModelProperty(value = "SKU数量", required = true)
    private String skuNum;
}

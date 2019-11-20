package com.cs.mobile.api.model.scm.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "StoreOrderReportResponse", description = "门店要货报表")
public class StoreOrderReportResponse {
    @ApiModelProperty(value = "下单门店数量", required = true)
    private Integer orderStoreNum;
    @ApiModelProperty(value = "未下单门店数量", required = true)
    private Integer noOrderStoreNum;
    @ApiModelProperty(value = "下单门店详情", required = true)
    private List<StoreOrderReportDetailResponse> orderList;
    @ApiModelProperty(value = "未下单门店详情", required = true)
    private List<StoreOrderReportDetailResponse> noOrderList;
}

package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreLossGoodsDetailResponse", description = "门店负毛利商品下钻明细")
public class StoreLossGoodsDetailResponse {
	private static final long serialVersionUID = 1L;
	
	
	@ApiModelProperty(value = "商品编码", required = true)
    private String item;
	
	@ApiModelProperty(value = "销售时间", required = true)
    private String saleDate;
    
	@ApiModelProperty(value = "商品名称", required = true)
    private String itemDesc;
    
    @ApiModelProperty(value = "数量", required = true)
    private String saleQty;
    
    @ApiModelProperty(value = "毛利额", required = true)
    private String grossProfit;
    
    @ApiModelProperty(value = "毛利率", required = true)
    private String gp;
    
    @ApiModelProperty(value = "销售额", required = true)
    private String salesVolume;
    
    @ApiModelProperty(value = "单位", required = true)
    private String unit;
}

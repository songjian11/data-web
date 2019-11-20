package com.cs.mobile.api.model.dailyreport.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreLossGoodsResponse", description = "门店负毛利商品top30")
public class StoreLossGoodsResponse {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "大类编码", required = true)
    private String dept;
	
	@ApiModelProperty(value = "商品编码", required = true)
    private String item;
    
	@ApiModelProperty(value = "商品名称", required = true)
    private String itemDesc;
    
    @ApiModelProperty(value = "销售类型", required = true)
    private String saleTypeDesc;
    
    @ApiModelProperty(value = "数量", required = true)
    private String saleQty;
    
    @ApiModelProperty(value = "未销售金额", required = true)
    private String gpRate;
    
    @ApiModelProperty(value = "毛利率", required = true)
    private String gp;
    
    @ApiModelProperty(value = "损失额", required = true)
    private String groupLoss;
    
    @ApiModelProperty(value = "单位", required = true)
    private String unit;
    
    

}

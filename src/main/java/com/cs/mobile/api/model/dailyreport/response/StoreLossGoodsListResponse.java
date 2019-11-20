package com.cs.mobile.api.model.dailyreport.response;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StoreLossGoodsListResponse", description = "门店负毛利商品top30日报")
public class StoreLossGoodsListResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "门店负毛利商品top30日报列表", required = true)
    private List<StoreLossGoodsResponse> StoreLossList;
	
	@ApiModelProperty(value = "门店负毛利商品top30日报汇总列表", required = true)
    private StoreLossGoodsSumResponse StoreLossSum;
	
	@ApiModelProperty(value = "门店负毛利商品下钻明细", required = true)
    private List<StoreLossGoodsDetailResponse> StoreLossDetailList;
	
	@ApiModelProperty(value = "商品毛利率卡片", required = true)
    private List<GrossProfitGoodsResponse> grossProfitGoodsList;
	
}

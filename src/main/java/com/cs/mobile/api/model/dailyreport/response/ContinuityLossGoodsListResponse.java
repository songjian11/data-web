package com.cs.mobile.api.model.dailyreport.response;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ContinuityLossGoodsListResponse", description = "门店连续负毛利商品")
public class ContinuityLossGoodsListResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "门店连续负毛利商品列表", required = true)
    private List<ContinuityLossGoodsResponse> continuityLossList;
	
	@ApiModelProperty(value = "门店连续负毛利商品列表汇总", required = true)
    private ContinuityLossGoodsDetailSumResponse continuityLossSum;
	
	@ApiModelProperty(value = "门店连续负毛利商品下钻列表", required = true)
    private List<ContinuityLossGoodsDetailResponse> continuityLossDetailList;
}

package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 连续负毛利商品下钻
 */
@Data
public class ContinuityLossGoodsDetailModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //销售日期
	private String saleDate;
	//数量
	private String saleQty;
	//销售额
	private String gpRate;
	//毛利率
	private String grossMargin;
	//损失额
	private String grossLoss;
}

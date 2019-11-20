package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 门店负毛利商品下钻
 */
@Data
public class StoreLossGoodsDetailModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //商品编码
    private String item;
    //销售时间
    private String saleDate;
    //商品名称
    private String itemDesc;
    //数量
    private String saleQty;
    //销售额
    private String salesVolume;
    //毛利额
    private String grossProfit;
    //毛利率
    private String gp;

}

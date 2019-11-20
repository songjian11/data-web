package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 门店负毛利商品top30日报
 */
@Data
public class StoreLossGoodsModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //大类编码
    private String dept;
    //商品编码
    private String item;
    //商品名称
    private String itemDesc;
    //销售类型
    private String saleTypeDesc;
    //数量
    private String saleQty;
    //未销售金额
    private String gpRate;
    //毛利率
    private String gp;
    // 损失额
    private String groupLoss;
    //单位
    private String unit;

}

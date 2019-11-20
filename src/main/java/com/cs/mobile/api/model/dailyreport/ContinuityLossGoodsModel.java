package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 连续负毛利商品
 */
@Data
public class ContinuityLossGoodsModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //大类编码
    private String dept;
    //商品编码
    private String item;
    //商品名称
    private String itemDesc;
    //销售类型
    private String saleTypeDesc;
    //次数
    private String frequency;

}

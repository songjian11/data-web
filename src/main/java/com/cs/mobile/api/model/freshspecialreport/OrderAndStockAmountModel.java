package com.cs.mobile.api.model.freshspecialreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 昨日订货和库存金额
 */
@Data
public class OrderAndStockAmountModel implements Serializable{
    private static final long serialVersionUID = 1L;

    private String id;
    //库存数量
    private String stock;
    //订货数量
    private String orderNum;
    //库存金额
    private String stockAmount;
    //订货金额
    private String orderAmount;
    //订货金额（含税）
    private String orderAmountIn;
}

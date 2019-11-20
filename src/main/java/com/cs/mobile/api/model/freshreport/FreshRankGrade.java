package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class FreshRankGrade implements Serializable {
    private static final long serialVersionUID = 1L;
    //名称
    private String id;
    //销售额
    private String totalSale;
    //扫描毛利额
    private String totalScanningRate;
    //扫描毛利率
    private String totalProfit;

    //含税销售额
    private String totalSaleIn;
    //含税扫描毛利额
    private String totalScanningRateIn;
    //含税扫描毛利率
    private String totalProfitIn;
}

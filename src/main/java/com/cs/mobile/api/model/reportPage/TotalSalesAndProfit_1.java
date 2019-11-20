package com.cs.mobile.api.model.reportPage;

import lombok.Data;

import java.io.Serializable;
@Data
public class TotalSalesAndProfit_1 implements Serializable {
    private static final long serialVersionUID = 1L;
    //销售额
    private String totalSale = "0";
    //前台毛利额
    private String totalFrontDeskRate = "0";
    //扫描毛利额
    private String totalScanningRate = "0";
    //最终毛利额
    private String totalRate = "0";
    //成本
    private String totalCost = "0";
    //同期销售额
    private String totalSameSale;
    //同期可比销售额
    private String totalCompareSale ;
    //同期前台毛利额
    private String totalSameFrontDeskRate = "0";
    //同期可比毛利额
    private String totalCompareFrontDeskRate = "0";
    //客单价
    private String customerSingerPrice = "0";
    //来客数
    private String kl = "0";
    //库存金额
    private String stockPrice = "0";
    //库存数量
    private String stockNum = "0";
    //周转天数
    private String turnoverDays = "0";
}

package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class FreshBaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //销售额
    private String totalSales = "0";
    //最终毛利额
    private String totalProfitPrice = "0";
    //扫描毛利额
    private String totalScanningProfitPrice = "0";
    //前台毛利额
    private String totalFrontDeskProfitPrice = "0";
    //损耗额
    private String lossPrice = "0";
    //折价额
    private String discountPrice = "0";
    //成本
    private String totalCost = "0";

    //销售额
    private String totalSalesIn = "0";
    //最终毛利额
    private String totalProfitPriceIn = "0";
    //扫描毛利额
    private String totalScanningProfitPriceIn = "0";
    //前台毛利额
    private String totalFrontDeskProfitPriceIn = "0";
    //损耗额
    private String lossPriceIn = "0";
    //折价额
    private String discountPriceIn = "0";
    //成本
    private String totalCostIn = "0";
}

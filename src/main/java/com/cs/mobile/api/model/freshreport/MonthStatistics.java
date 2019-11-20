package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;

@Data
public class MonthStatistics implements Serializable {
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

    //含税销售额
    private String totalSalesIn = "0";
    //含税最终毛利额
    private String totalProfitPriceIn = "0";
    //含税扫描毛利额
    private String totalScanningProfitPriceIn = "0";
    //含税前台毛利额
    private String totalFrontDeskProfitPriceIn = "0";
    //含税损耗额
    private String lossPriceIn = "0";
    //含税折价额
    private String discountPriceIn = "0";
}

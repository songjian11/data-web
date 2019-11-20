package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class SalesList implements Serializable {
    private static final long serialVersionUID = 1L;
    //时间(yyyyMMdd)
    private String time;
    //销售额
    private String totalSales = "0";
    //毛利额
    private String totalProfitPrice = "0";
    //前台毛利额
    private String totalFrontDeskRate = "0";
    //扫描毛利额
    private String totalScanningRate = "0";
    //损耗
    private String lossPrice = "0";

    //含税销售额
    private String totalSalesIn = "0";
    //含税毛利额
    private String totalProfitPriceIn = "0";
    //含税前台毛利额
    private String totalFrontDeskRateIn = "0";
    //含税扫描毛利额
    private String totalScanningRateIn = "0";
    //含税损耗
    private String lossPriceIn = "0";
    //客流
    private String Kl = "0";
}

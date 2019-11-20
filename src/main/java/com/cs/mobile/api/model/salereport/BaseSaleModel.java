package com.cs.mobile.api.model.salereport;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseSaleModel implements Serializable{
    private static final long serialVersionUID = 1L;
    //销售额
    private String totalSale = "0";
    //最终毛利额=(前台毛利额 + 扫描毛利额)或者等于其一
    private String totalRate = "0";
    //前台毛利额
    private String totalFrontDeskRate = "0";
    //扫描毛利额
    private String totalScanningRate = "0";
    //成本
    private String totalCost = "0";
    //可比销售额
    private String totalCompareSale = "0";
    //可比前台毛利额
    private String totalCompareFrontDeskRate = "0";
    //可比扫描毛利额
    private String totalCompareScanningRate = "0";
    //可比最终毛利额=(可比前台毛利额 + 可比扫描毛利额)或者等于其一
    private String totalCompareRate = "0";
    //可比成本
    private String totalCompareCost = "0";
    //含税销售额
    private String totalSaleIn = "0";
    //含税最终毛利额=(前台毛利额 + 扫描毛利额)或者等于其一
    private String totalRateIn = "0";
    //含税前台毛利额
    private String totalFrontDeskRateIn = "0";
    //含税扫描毛利额
    private String totalScanningRateIn = "0";
    //含税成本
    private String totalCostIn = "0";
    //含税可比销售额
    private String totalCompareSaleIn = "0";
    //含税可比前台毛利额
    private String totalCompareFrontDeskRateIn = "0";
    //含税可比扫描毛利额
    private String totalCompareScanningRateIn = "0";
    //含税可比最终毛利额=(可比前台毛利额 + 可比扫描毛利额)或者等于其一
    private String totalCompareRateIn = "0";
    //含税可比成本
    private String totalCompareCostIn = "0";
}

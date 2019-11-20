package com.cs.mobile.api.model.salereport;

import lombok.Data;

import java.io.Serializable;
@Data
public class ItemRankModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //大类ID
    private String deptId;
    //大类名称
    private String deptName;
    //单品ID
    private String itemId;
    //单品名称
    private String itemName;
    //销售额
    private String totalSale = "0";
    //最终毛利额
    private String totalRate = "0";
    //最终可比毛利额
    private String totalCompareRate = "0";
    //前台毛利额
    private String totalFrontDeskRate = "0";
    //扫描毛利额
    private String totalScanningRate = "0";
    //可比销售额
    private String totalCompareSale = "0";
    //可比扫描毛利额
    private String totalCompareScanningRate = "0";
    //可比扫描前台毛利额
    private String totalCompareFrontDeskRate = "0";
    //含税销售额
    private String totalSaleIn = "0";
    //含税最终毛利额
    private String totalRateIn = "0";
    //含税最终可比毛利额
    private String totalCompareRateIn = "0";
    //含税前台毛利额
    private String totalFrontDeskRateIn = "0";
    //含税扫描毛利额
    private String totalScanningRateIn = "0";
    //含税可比销售额
    private String totalCompareSaleIn = "0";
    //含税可比扫描毛利额
    private String totalCompareScanningRateIn = "0";
    //含税可比扫描前台毛利额
    private String totalCompareFrontDeskRateIn = "0";
    //排名
    private String rn;
}

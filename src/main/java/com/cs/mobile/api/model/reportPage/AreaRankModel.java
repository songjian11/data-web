package com.cs.mobile.api.model.reportPage;

import lombok.Data;

import java.io.Serializable;
@Data
public class AreaRankModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //区域ID
    private String id;
    //销售额
    private String totalSale;
    //扫描毛利额
    private String totalScanningRate;
    //前台毛利额
    private String totalFrontDeskRate;
    //计算使用毛利额
    private String totalRate;
    //可比销售额
    private String totalCompareSale;
    //可比扫描毛利额
    private String totalCompareScanningRate;
    //可比前台毛利额
    private String totalCompareFrontDeskRate;
    //计算使用可比毛利额
    private String totalCompareRate;
    //含税销售额
    private String totalSaleIn;
    //含税扫描毛利额
    private String totalScanningRateIn;
    //含税前台毛利额
    private String totalFrontDeskRateIn;
    //含税计算使用毛利额
    private String totalRateIn;
    //含税可比销售额
    private String totalCompareSaleIn;
    //含税可比扫描毛利额
    private String totalCompareScanningRateIn;
    //含税可比前台毛利额
    private String totalCompareFrontDeskRateIn;
    //含税计算使用可比毛利额
    private String totalCompareRateIn;
    //客流
    private String kl;
}

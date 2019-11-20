package com.cs.mobile.api.model.salereport;

import lombok.Data;

import java.io.Serializable;
@Data
public class SaleCompositionModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String totalSale = "0";

    private String totalRate = "0";

    //前台毛利额
    private String totalFrontDeskRate = "0";
    //扫描毛利额
    private String totalScanningRate = "0";

    private String totalCost = "0";

    private String totalSaleIn = "0";

    private String totalRateIn = "0";

    //前台毛利额
    private String totalFrontDeskRateIn = "0";
    //扫描毛利额
    private String totalScanningRateIn = "0";

    private String totalCostIn = "0";
    //类型：1-正常 2-大宗 3-折价,5-促销
    private String type;
}

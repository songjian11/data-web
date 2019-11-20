package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class FreshRankInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String totalSales;

    private String lossPrice;

    //最终毛利额（历史默认前台毛利额）
    private String totalProfitPrice;
    //扫描毛利额
    private String totalScanningProfitPrice;

    private String discountPrice;

    //含税销售额
    private String totalSalesIn;
    //含税损耗率
    private String lossPriceIn;
    //含税最终毛利额（历史默认前台毛利额）
    private String totalProfitPriceIn;
    //含税扫描毛利额
    private String totalScanningProfitPriceIn;
    //含税折价额
    private String discountPriceIn;

    private String id;
    //可比标志
    private String compareMark;
}



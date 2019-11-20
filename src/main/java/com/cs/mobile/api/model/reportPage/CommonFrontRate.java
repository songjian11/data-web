package com.cs.mobile.api.model.reportPage;

import lombok.Data;

import java.io.Serializable;
@Data
public class CommonFrontRate implements Serializable {
    private static final long serialVersionUID = 1L;
    //品类
    private String category;
    //大类ID
    private String deptId;
    //门店ID
    private String storeId;
    //门店名称
    private String storeName;
    //时间点
    private String timePoint;
    //具体时间（日线的时候使用，查看时间点属于哪年哪月哪天）
    private String time;
    //销售
    private String totalSales;
    //前台毛利额
    private String totalFrontDeskRate;
    //成本
    private String totalCost;
}

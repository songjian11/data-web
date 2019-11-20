package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 门店战区报表
 */
@Data
public class StoreFreshDailyReport implements Serializable {
    private static final long serialVersionUID = 1L;
    //门店名称
    private String storeName;
    //属性
    private String typeName;
    //负责人
    private String userName;
    //昨日销售额
    private String daySale;
    //去年同期销售额
    private String daySameSale;
    //昨日销售增长率
    private String daySaleRate;
    //昨日前台毛利率
    private String dayProfit;
    //去年同期前台毛利率
    private String daySameProfit;
    //昨日前台毛利率增长
    private String dayProfitRate;
    //日渗透率
    private String dayKlRate;
    //当月累计销售额
    private String monthSale;
    //去年同月累计销售额
    private String monthSameSale;
    //当月累计销售增长率
    private String monthSaleRate;
    //当月累计前台毛利率
    private String monthProfit;
    //去年同月前台毛利率
    private String monthSameProfit;
    //当月前台毛利率增长
    private String monthProfitRate;
    //月渗透率
    private String monthKlRate;
}

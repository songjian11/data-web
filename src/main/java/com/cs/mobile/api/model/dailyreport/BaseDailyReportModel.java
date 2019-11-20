package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础信息
 */
@Data
public class BaseDailyReportModel implements Serializable{
    private static final long serialVersionUID = 1L;
    //属性名称
    private String typeName;
    //昨日销售额
    private String daySale;
    //去年同期销售额
    private String sameSale;
    //昨日销售增长率
    private String daySaleRate;
    //当月累计销售额
    private String monthSale;
    //去年同月累计销售额
    private String monthSameSale;
    //当月累计销售增长率
    private String monthSaleRate;
    //昨日前台毛利率
    private String dayProfit;
    //去年同期前台毛利率
    private String sameProfit;
    //昨日前台毛利率增长
    private String dayProfitRate;
    //当月累计前台毛利率
    private String monthProfit;
    //去年同月前台毛利率
    private String monthSameProfit;
    //当月前台毛利率增长
    private String monthProfitRate;
    //日渗透率
    private String dayKlRate;
    //月渗透率
    private String monthKlRate;
}

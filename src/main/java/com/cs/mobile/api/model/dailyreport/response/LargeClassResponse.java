package com.cs.mobile.api.model.dailyreport.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "LargeClassResponse", description = "大类报表")
public class LargeClassResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //省份
    @ApiModelProperty(value = "省份", required = true)
    private String provinceName;
    
    //区域
    @ApiModelProperty(value = "区域", required = true)
    private String areaName;
    
    //属性
    @ApiModelProperty(value = "属性", required = true)
    private String typeName;
    
    //门店编码
    @ApiModelProperty(value = "门店编码", required = true)
    private String storeId;
    
    //当月累计销售额
    @ApiModelProperty(value = "门店名称", required = true)
    private String storeName;
    
    //大类编码
    @ApiModelProperty(value = "大类编码", required = true)
    private String deptId;
    
    // 大类名称
    @ApiModelProperty(value = "大类名称", required = true)
    private String deptName;
    
    //昨日销售额
    @ApiModelProperty(value = "昨日销售额", required = true)
    private String daySale;
    
    //去年同期销售额
    @ApiModelProperty(value = "去年同期销售额", required = true)
    private String sameSale;
    
    //昨日销售增长率
    @ApiModelProperty(value = "昨日销售增长率", required = true)
    private String daySaleRate;
    
    //当月累计销售额
    @ApiModelProperty(value = "当月累计销售额", required = true)
    private String monthSale;
    
    //去年同月累计销售额
    @ApiModelProperty(value = "去年同月累计销售额", required = true)
    private String monthSameSale;
    
    //当月累计销售增长率
    @ApiModelProperty(value = "当月累计销售增长率", required = true)
    private String monthSaleRate;
    
    //昨日前台毛利率
    @ApiModelProperty(value = "昨日前台毛利率", required = true)
    private String dayProfit;
    
    //去年同期前台毛利率
    @ApiModelProperty(value = "去年同期前台毛利率", required = true)
    private String sameProfit;
    
    //昨日前台毛利率增长
    @ApiModelProperty(value = "昨日前台毛利率增长", required = true)
    private String dayProfitRate;
    
    //当月累计前台毛利率
    @ApiModelProperty(value = "当月累计前台毛利率", required = true)
    private String monthProfit;
    
    //去年同月前台毛利率
    @ApiModelProperty(value = "去年同月前台毛利率", required = true)
    private String monthSameProfit;
    
    //当月前台毛利率增长
    @ApiModelProperty(value = "当月前台毛利率增长", required = true)
    private String monthProfitRate;
    
    //昨日客流
    @ApiModelProperty(value = "昨日客流", required = true)
    private String kl;
    
    //昨日大类客流
    @ApiModelProperty(value = "昨日大类客流", required = true)
    private String deptKl;
    
    //月至今客流
    @ApiModelProperty(value = "月至今客流", required = true)
    private String monthKl;
    
    //月大类客流
    @ApiModelProperty(value = "月大类客流", required = true)
    private String monthDeptKl;
    
    //日渗透率
    @ApiModelProperty(value = "日渗透率", required = true)
    private String dayKlRate;
    
    //月渗透率
    @ApiModelProperty(value = "月渗透率", required = true)
    private String monthKlRate;
    
}

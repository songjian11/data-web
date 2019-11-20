package com.cs.mobile.api.model.reportPage;

import com.cs.mobile.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TotalSalesAndProfit implements Serializable {
    private static final long serialVersionUID = 1L;
    //销售额
    private String totalSales = "0";
    //前台毛利额
    private String totalFrontDeskRate = "0";
    //扫描毛利率
    private String scanningProfitRate = "0";
    //前台毛利率
    private String frontDeskProfitRate = "0";
    //最终毛利额
    private String totalRate = "0";
    //毛利率
    private String totalProfit = "0";
    //销售额达成率
    private String salesAchievingRate = "0";
    //毛利率达成率
    private String profitAchievingRate = "0";
    //销售额同比
    private String salesSameRate ;
    //毛利率同比
    private String profitSameRate ;
    //销售额可比
    private String salesCompareRate ;
    //毛利率可比
    private String profitCompareRate ;
    //客单价
    private String customerSingerPrice = "0";
    //来客数
    private String customerNum = "0";
    //库存金额
    private String stockPrice = "0";
    //库存数量
    private String stockNum = "0";
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

    private String totalSalesIn = "0";
    //前台毛利额
    private String totalFrontDeskRateIn = "0";
    //扫描毛利率
    private String scanningProfitRateIn = "0";
    //前台毛利率
    private String frontDeskProfitRateIn = "0";
    //最终毛利额
    private String totalRateIn = "0";
    //毛利率
    private String totalProfitIn = "0";
    //销售额达成率
    private String salesAchievingRateIn = "0";
    //毛利率达成率
    private String profitAchievingRateIn = "0";
    //销售额同比
    private String salesSameRateIn ;
    //毛利率同比
    private String profitSameRateIn ;
    //销售额可比
    private String salesCompareRateIn ;
    //毛利率可比
    private String profitCompareRateIn ;
    //客单价
    private String customerSingerPriceIn = "0";
    //成本
    private String totalCostIn = "0";
    //可比销售额
    private String totalCompareSaleIn = "0";
    //可比前台毛利额
    private String totalCompareFrontDeskRateIn = "0";
    //可比扫描毛利额
    private String totalCompareScanningRateIn = "0";
    //可比最终毛利额=(可比前台毛利额 + 可比扫描毛利额)或者等于其一
    private String totalCompareRateIn = "0";
    //可比成本
    private String totalCompareCostIn = "0";
}

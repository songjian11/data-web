package com.cs.mobile.api.model.reportPage;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class NotFresh implements Serializable {
    private static final long serialVersionUID = 1L;
    //销售额
    private String totalSales = "0";
    //毛利率
    private String totalProfit = "0";
    //毛利额
    private String totalProfitPrice = "0";
    //销售额达成率
    private String salesAchievingRate = "0";
    //客单价
    private String customerSingerPrice = "0";
    //来客数
    private String customerNum = "0";
    //扫描毛利率
    private String lossPrice = "0";
    //库存金额
    private String lossRate = "0";

    //销售额
    private String totalSalesIn = "0";
    //毛利率
    private String totalProfitIn = "0";
    //毛利额
    private String totalProfitPriceIn = "0";
    //销售额达成率
    private String salesAchievingRateIn = "0";
    //客单价
    private String customerSingerPriceIn = "0";
    //扫描毛利率
    private String lossPriceIn = "0";
    //库存金额
    private String lossRateIn = "0";

}

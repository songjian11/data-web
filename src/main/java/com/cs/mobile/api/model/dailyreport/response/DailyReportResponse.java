package com.cs.mobile.api.model.dailyreport.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "DailyReportResponse", description = "日报基础数据")
public class DailyReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    //属性名称
    @ApiModelProperty(value = "属性名称", required = true)
    private String typeName;
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
    @ApiModelProperty(value = "去年同期销售额", required = true)
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
    //昨日品类客流
    @ApiModelProperty(value = "昨日品类客流", required = true)
    private String categoryKl;
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

    @ApiModelProperty(value = "梯度,下钻时取当前值加1(1-全司，2-省份，3-区域,4-门店)", required = true)
    private int mark;

    public String getDaySale() {
        if("%".equals(daySale)){
            return "";
        }
        return daySale;
    }

    public String getSameSale() {
        if("%".equals(sameSale)){
            return "";
        }
        return sameSale;
    }

    public String getDaySaleRate() {
        if("%".equals(daySaleRate)){
            return "";
        }
        return daySaleRate;
    }

    public String getMonthSale() {
        if("%".equals(monthSale)){
            return "";
        }
        return monthSale;
    }

    public String getMonthSameSale() {
        if("%".equals(monthSameSale)){
            return "";
        }
        return monthSameSale;
    }

    public String getMonthSaleRate() {
        if("%".equals(monthSaleRate)){
            return "";
        }
        return monthSaleRate;
    }

    public String getDayProfit() {
        if("%".equals(dayProfit)){
            return "";
        }
        return dayProfit;
    }

    public String getSameProfit() {
        if("%".equals(sameProfit)){
            return "";
        }
        return sameProfit;
    }

    public String getDayProfitRate() {
        if("%".equals(dayProfitRate)){
            return "";
        }
        return dayProfitRate;
    }

    public String getMonthProfit() {
        if("%".equals(monthProfit)){
            return "";
        }
        return monthProfit;
    }

    public String getMonthSameProfit() {
        if("%".equals(monthSameProfit)){
            return "";
        }
        return monthSameProfit;
    }

    public String getMonthProfitRate() {
        if("%".equals(monthProfitRate)){
            return "";
        }
        return monthProfitRate;
    }

    public String getKl() {
        if("%".equals(kl)){
            return "";
        }
        return kl;
    }

    public String getCategoryKl() {
        if("%".equals(categoryKl)){
            return "";
        }
        return categoryKl;
    }

    public String getDeptKl() {
        if("%".equals(deptKl)){
            return "";
        }
        return deptKl;
    }

    public String getMonthKl() {
        if("%".equals(monthKl)){
            return "";
        }
        return monthKl;
    }

    public String getMonthDeptKl() {
        if("%".equals(monthDeptKl)){
            return "";
        }
        return monthDeptKl;
    }

    public String getDayKlRate() {
        String value = "";
        if("%".equals(dayKlRate)){
            value =  "";
        }else if(StringUtils.isNotEmpty(dayKlRate) && !dayKlRate.endsWith("%")){
            BigDecimal bg = new BigDecimal(dayKlRate).multiply(new BigDecimal("100")).divide(BigDecimal.ONE,1,BigDecimal.ROUND_HALF_UP);
            value = bg.toString() + "%";
        }else{
            value = dayKlRate;
        }
        return value;
    }

    public String getMonthKlRate() {
        String value = "";
        if("%".equals(monthKlRate)){
            value =  "";
        }else if(StringUtils.isNotEmpty(monthKlRate) && !monthKlRate.endsWith("%")){
            BigDecimal bg = new BigDecimal(monthKlRate).multiply(new BigDecimal("100")).divide(BigDecimal.ONE,1,BigDecimal.ROUND_HALF_UP);
            value = bg.toString() + "%";
        }else{
            value = monthKlRate;
        }
        return value;
    }
}

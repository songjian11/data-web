package com.cs.mobile.api.model.dailyreport.response;

import com.cs.mobile.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
@ApiModel(value = "StoreFreshDailyReportResponse", description = "生鲜战区报表")
public class StoreFreshDailyReportResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "门店名称", required = true)
    private String storeName;
    //属性
    @ApiModelProperty(value = "属性", required = true)
    private String typeName;
    //最坏销售
    @ApiModelProperty(value = "最坏", required = true)
    private String dayWorst;
    //最好销售
    @ApiModelProperty(value = "最好", required = true)
    private String dayBest;
    //月最坏销售
    @ApiModelProperty(value = "月最坏", required = true)
    private String monthWorst;
    //月最好销售
    @ApiModelProperty(value = "月最好", required = true)
    private String monthBest;
    //负责人
    @ApiModelProperty(value = "负责人", required = true)
    private String userName;
    //昨日销售额
    @ApiModelProperty(value = "昨日销售额", required = true)
    private String daySale;
    //去年同期销售额
    @ApiModelProperty(value = "去年同期销售额", required = true)
    private String daySameSale;
    //昨日销售增长率
    @ApiModelProperty(value = "昨日销售增长率", required = true)
    private String daySaleRate;
    //昨日前台毛利率
    @ApiModelProperty(value = "昨日前台毛利率", required = true)
    private String dayProfit;
    //去年同期前台毛利率
    @ApiModelProperty(value = "去年同期前台毛利率", required = true)
    private String daySameProfit;
    //昨日前台毛利率增长
    @ApiModelProperty(value = "昨日前台毛利率增长", required = true)
    private String dayProfitRate;
    //日渗透率
    @ApiModelProperty(value = "日渗透率", required = true)
    private String dayKlRate;
    //当月累计销售额
    @ApiModelProperty(value = "当月累计销售额", required = true)
    private String monthSale;
    //去年同月累计销售额
    @ApiModelProperty(value = "去年同月累计销售额", required = true)
    private String monthSameSale;
    //当月累计销售增长率
    @ApiModelProperty(value = "当月累计销售增长率", required = true)
    private String monthSaleRate;
    //当月累计前台毛利率
    @ApiModelProperty(value = "当月累计前台毛利率", required = true)
    private String monthProfit;
    //去年同月前台毛利率
    @ApiModelProperty(value = "去年同月前台毛利率", required = true)
    private String monthSameProfit;
    //当月前台毛利率增长
    @ApiModelProperty(value = "当月前台毛利率增长", required = true)
    private String monthProfitRate;
    //月渗透率
    @ApiModelProperty(value = "月渗透率", required = true)
    private String monthKlRate;

    public String getDaySale() {
        if("%".equals(daySale)){
            return "";
        }
        return daySale;
    }

    public String getDaySameSale() {
        if("%".equals(daySameSale)){
            return "";
        }
        return daySameSale;
    }

    public String getDaySaleRate() {
        String value = daySaleRate;
        if("%".equals(value)){
            return "";
        }
        if(value.contains(".")){
            String str1 = value.substring(0,value.lastIndexOf("."));
            if(StringUtils.isEmpty(str1)){
                return "0" + value;
            }
            if("-".equals(str1)){
                return "-0" + value.substring(value.lastIndexOf("-") + 1);
            }
            String str2 = value.substring(value.lastIndexOf(".") + 1);
            if("%".equals(str2)){
                return value.substring(0, value.lastIndexOf("%")) + "0%";
            }
        }
        return value;
    }

    public String getDayProfit() {
        String value = dayProfit;
        if("%".equals(value)){
            return "";
        }
        if(value.contains(".")){
            String str1 = value.substring(0,value.lastIndexOf("."));
            if(StringUtils.isEmpty(str1)){
                return "0" + value;
            }
            if("-".equals(str1)){
                return "-0" + value.substring(value.lastIndexOf("-") + 1);
            }
            String str2 = value.substring(value.lastIndexOf(".") + 1);
            if("%".equals(str2)){
                return value.substring(0, value.lastIndexOf("%")) + "0%";
            }
        }
        return value;
    }

    public String getDaySameProfit() {
        String value = daySameProfit;
        if("%".equals(value)){
            return "";
        }
        if(value.contains(".")){
            String str1 = value.substring(0,value.lastIndexOf("."));
            if(StringUtils.isEmpty(str1)){
                return "0" + value;
            }
            if("-".equals(str1)){
                return "-0" + value.substring(value.lastIndexOf("-") + 1);
            }
            String str2 = value.substring(value.lastIndexOf(".") + 1);
            if("%".equals(str2)){
                return value.substring(0, value.lastIndexOf("%")) + "0%";
            }
        }
        return value;
    }

    public String getDayProfitRate() {
        String value = dayProfitRate;
        if("%".equals(value)){
            return "";
        }
        if(value.contains(".")){
            String str1 = value.substring(0,value.lastIndexOf("."));
            if(StringUtils.isEmpty(str1)){
                return "0" + value;
            }
            if("-".equals(str1)){
                return "-0" + value.substring(value.lastIndexOf("-") + 1);
            }
            String str2 = value.substring(value.lastIndexOf(".") + 1);
            if("%".equals(str2)){
                return value.substring(0, value.lastIndexOf("%")) + "0%";
            }
        }
        return value;
    }

    public String getDayKlRate() {
        String value = "";
        if("%".equals(dayKlRate)){
            value =  "";
        }else if(StringUtils.isNotEmpty(dayKlRate) && !dayKlRate.endsWith("%")){
            BigDecimal bg = new BigDecimal(dayKlRate).multiply(new BigDecimal("100")).divide(BigDecimal.ONE,1,BigDecimal.ROUND_HALF_UP);
            value = bg.toString();
        }else if(StringUtils.isNotEmpty(dayKlRate) && dayKlRate.endsWith("%")){
            value = dayKlRate.substring(0, dayKlRate.lastIndexOf("%"));
        }else{
            value = dayKlRate;
        }
        return value;
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
        String value = monthSaleRate;
        if("%".equals(value)){
            return "";
        }
        if(value.contains(".")){
            String str1 = value.substring(0,value.lastIndexOf("."));
            if(StringUtils.isEmpty(str1)){
                return "0" + value;
            }
            if("-".equals(str1)){
                return "-0" + value.substring(value.lastIndexOf("-") + 1);
            }
            String str2 = value.substring(value.lastIndexOf(".") + 1);
            if("%".equals(str2)){
                return value.substring(0, value.lastIndexOf("%")) + "0%";
            }
        }
        return value;
    }

    public String getMonthProfit() {
        String value = monthProfit;
        if("%".equals(value)){
            return "";
        }
        if(value.contains(".")){
            String str1 = value.substring(0,value.lastIndexOf("."));
            if(StringUtils.isEmpty(str1)){
                return "0" + value;
            }
            if("-".equals(str1)){
                return "-0" + value.substring(value.lastIndexOf("-") + 1);
            }
            String str2 = value.substring(value.lastIndexOf(".") + 1);
            if("%".equals(str2)){
                return value.substring(0, value.lastIndexOf("%")) + "0%";
            }
        }
        return value;
    }

    public String getMonthSameProfit() {
        String value = monthSameProfit;
        if("%".equals(value)){
            return "";
        }
        if(value.contains(".")){
            String str1 = value.substring(0,value.lastIndexOf("."));
            if(StringUtils.isEmpty(str1)){
                return "0" + value;
            }
            if("-".equals(str1)){
                return "-0" + value.substring(value.lastIndexOf("-") + 1);
            }
            String str2 = value.substring(value.lastIndexOf(".") + 1);
            if("%".equals(str2)){
                return value.substring(0, value.lastIndexOf("%")) + "0%";
            }
        }
        return value;
    }

    public String getMonthProfitRate() {
        String value = monthProfitRate;
        if("%".equals(value)){
            return "";
        }
        if(value.contains(".")){
            String str1 = value.substring(0,value.lastIndexOf("."));
            if(StringUtils.isEmpty(str1)){
                return "0" + value;
            }
            if("-".equals(str1)){
                return "-0" + value.substring(value.lastIndexOf("-") + 1);
            }
            String str2 = value.substring(value.lastIndexOf(".") + 1);
            if("%".equals(str2)){
                return value.substring(0, value.lastIndexOf("%")) + "0%";
            }
        }
        return value;
    }

    public String getMonthKlRate() {
        String value = "";
        if("%".equals(monthKlRate)){
            value =  "";
        }else if(StringUtils.isNotEmpty(monthKlRate) && !monthKlRate.endsWith("%")){
            BigDecimal bg = new BigDecimal(monthKlRate).multiply(new BigDecimal("100")).divide(BigDecimal.ONE,1,BigDecimal.ROUND_HALF_UP);
            value = bg.toString();
        }else if(StringUtils.isNotEmpty(monthKlRate) && monthKlRate.endsWith("%")){
            value = monthKlRate.substring(0, monthKlRate.lastIndexOf("%"));
        }else{
            value = monthKlRate;
        }
        return value;
    }
}

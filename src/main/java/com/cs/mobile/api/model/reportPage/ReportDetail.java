package com.cs.mobile.api.model.reportPage;

import com.cs.mobile.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ReportDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    //时间点
    private String timePoint;
    //具体时间（日线的时候使用，查看时间点属于哪年哪月哪天yyyyMMdd）
    private String time;
    //销售额
    private String totalSales = "0";
    //毛利率
    private String totalprofit = "0";
    //毛利额
    private String totalRate = "0";
    //前台毛利额
    private String totalFrontDeskRate = "0";
    //总成本
    private String totalCost = "0";

    //销售额
    private String totalSalesIn = "0";
    //毛利率
    private String totalprofitIn = "0";
    //毛利额
    private String totalRateIn = "0";
    //前台毛利额
    private String totalFrontDeskRateIn = "0";
    //总成本
    private String totalCostIn = "0";
}


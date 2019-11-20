package com.cs.mobile.api.model.reportPage;

import com.cs.mobile.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RankDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    //门店名称
    private String storeName;
    //门店ID
    private String storeId;
    //区域名称
    private String areaName;
    //区域ID
    private String areaId;
    //品类名称
    private String deptName;
    //大类ID
    private String deptId;
    //大类名称
    private String majorDeptName;

    //销售额
    private String totalSales = "0";
    //毛利额
    private String totalRate = "0";
    //前台毛利额
    private String totalFrontDeskRate = "0";

    //可比销售额
    private String totalCompareSales = "0";
    //最终可比毛利额
    private String totalCompareRate = "0";
    //可比前台毛利额
    private String totalCompareFrontDeskRate = "0";

    // 客流
    private String customerNum = "0";
    //成本
    private String totalCost = "0";
    //可比成本
    private String totalCompareCost = "0";

    //销售额的同比
    private String saleSameRate;
    //毛利额的同比
    private String profitSameRate;

    //销售额的可比
    private String saleCompareRate;
    //毛利额的可比
    private String profitCompareRate;

    //销售额
    private String totalSalesIn = "0";
    //毛利额
    private String totalRateIn = "0";
    //前台毛利额
    private String totalFrontDeskRateIn = "0";

    //可比销售额
    private String totalCompareSalesIn = "0";
    //最终可比毛利额
    private String totalCompareRateIn = "0";
    //可比前台毛利额
    private String totalCompareFrontDeskRateIn = "0";

    //成本
    private String totalCostIn = "0";
    //可比成本
    private String totalCompareCostIn = "0";

    //销售额的同比
    private String saleSameRateIn;
    //毛利额的同比
    private String profitSameRateIn;

    //销售额的可比
    private String saleCompareRateIn;
    //毛利额的可比
    private String profitCompareRateIn;
}

package com.cs.mobile.api.model.reportPage;

import com.cs.mobile.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HomeAppliance implements Serializable {
    private static final long serialVersionUID = 1L;
    //销售额
    private String totalSale ="0";

    //毛利率
    private String totalProfit ="0";

    //成本
    private String totalCost ="0";

    private String totalRate = "0";

    //含税销售额
    private String totalSaleIn ="0";

    //含税毛利率
    private String totalProfitIn ="0";

    //含税成本
    private String totalCostIn ="0";

    //含税毛利额
    private String totalRateIn = "0";
}

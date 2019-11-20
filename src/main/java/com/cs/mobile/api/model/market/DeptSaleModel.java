package com.cs.mobile.api.model.market;

import lombok.Data;

import java.io.Serializable;
/**
 * 大类统计数据
 */
@Data
public class DeptSaleModel  implements Serializable {
    private static final long serialVersionUID = 1L;
    //品类名称
    private String categoryName;
    //部门ID
    private String purchaseDept;
    //部门名称
    private String purchaseDeptName;
    //大类名称
    private String deptName;
    //大类ID
    private String deptId;
    //销售
    private String sale;
    //含税销售
    private String saleIn;
    //可比销售
    private String compareSale;
    //含税可比销售
    private String compareSaleIn;
    //毛利额
    private String rate;
    //含税毛利额
    private String rateIn;
    //可比毛利额
    private String compareRate;
    //含税可比毛利额
    private String compareRateIn;
    //客流
    private String kl;
}

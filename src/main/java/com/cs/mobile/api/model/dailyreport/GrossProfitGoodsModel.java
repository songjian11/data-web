package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 单品毛利率卡片
 */
@Data
public class GrossProfitGoodsModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //门店毛利率
    private String storeGp;
    //区域毛利率
    private String areaGp;
    //省份毛利率
    private String provinceGp;
    //数量
    private String allGp;
   

}

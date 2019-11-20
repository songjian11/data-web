package com.cs.mobile.api.model.freshspecialreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 生鲜销售和毛利额信息
 */
@Data
public class FreshSaleAndRateModel implements Serializable{
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String kl;

    private String sale;

    private String rate;

    private String compareSale;

    private String compareRate;

    private String saleIn;

    private String rateIn;

    private String compareSaleIn;

    private String compareRateIn;
}

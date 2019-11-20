package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 门店负责人
 */
@Data
public class StorePersonModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //门店名称
    private String storeName;
    //负责人
    private String userName;
}

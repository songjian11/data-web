package com.cs.mobile.api.model.scm;

import lombok.Data;

@Data
public class StoreOrderReport {
    private String storeId;
    private String storeName;
    private String orderNum;
    private String skuNum;
}

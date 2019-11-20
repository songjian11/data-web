package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class OrganizationForFresh implements Serializable {
    private static final long serialVersionUID = 1L;

    private String provinceId;

    private String provinceName;

    private String areaId;

    private String areaName;

    private String storeId;

    private String storeName;
}

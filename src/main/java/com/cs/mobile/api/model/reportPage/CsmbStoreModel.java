package com.cs.mobile.api.model.reportPage;

import lombok.Data;

import java.io.Serializable;
@Data
public class CsmbStoreModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //省份编码
    private String provinceId;
    
    //省份名称
    private String provinceName;
    
    //区域编码
    private String areaId;
    
    //区域名称
    private String areaName;
    
    //门店编码
    private String storeId;
    
    //门店名称
    private String storeName;
   
}

package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 门店日报表
 */
@Data
public class StoreDailyReportModel extends BaseDailyReportModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //省份名称
    private String provinceName;
    //省ID
    private String provinceId;
    //区域名称
    private String areaName;
    //区域ID
    private String areaId;
    //门店ID
    private String storeId;
    //门店名称
    private String storeName;
    //大类名称
    private String deptName;
    //大类ID
    private String deptId;

    //昨日客流
    private String kl;
    //昨日大类客流
    private String deptKl;
    //月至今客流
    private String monthKl;
    //月大类客流
    private String monthDeptKl;
}

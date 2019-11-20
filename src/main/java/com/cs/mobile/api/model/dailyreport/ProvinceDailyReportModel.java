package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 省份日报表
 */
@Data
public class ProvinceDailyReportModel extends BaseDailyReportModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //省份名称
    private String provinceName;
    //省ID
    private String provinceId;
    //大类名称
    private String deptName;
    //大类ID
    private String deptId;

    //昨日客流
    private String kl;

    //昨日大类客流
    private String deptKl;
}

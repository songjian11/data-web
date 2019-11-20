package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 全司日报表
 */
@Data
public class CompanyDailyReportModel extends BaseDailyReportModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //省份名称
    private String provinceName;
    //省ID
    private String provinceId;

    //昨日客流
    private String kl;
    //昨日品类客流
    private String categoryKl;
}

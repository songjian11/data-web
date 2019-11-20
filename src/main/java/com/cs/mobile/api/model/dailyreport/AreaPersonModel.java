package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 区域负责人
 */
@Data
public class AreaPersonModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //区域名称
    private String areaName;
    //负责人
    private String userName;
}

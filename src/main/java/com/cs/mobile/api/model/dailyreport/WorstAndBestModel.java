package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

@Data
public class WorstAndBestModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //组织机构关联ID
    private String relateId;
    //组织机构关联名称
    private String relateName;
    //单品/大类ID
    private String id;
    //单品/大类名称
    private String name;
}

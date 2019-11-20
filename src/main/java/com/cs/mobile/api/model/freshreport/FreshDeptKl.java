package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class FreshDeptKl implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deptId;

    private String deptName;
    //时间（yyyyMMdd）
    private String time;
    //生鲜会员客流
    private String freshMemberCount;
    //生鲜客流
    private String freshKlCount;
    //总客流
    private String allKlCount;
}

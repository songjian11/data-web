package com.cs.mobile.api.model.freshreport;

import lombok.Data;

import java.io.Serializable;
@Data
public class FreshKl implements Serializable {
    private static final long serialVersionUID = 1L;
    //时间（yyyyMMdd）
    private String time;
    //生鲜会员客流
    private String freshMemberCount;
    //总客流
    private String allKlCount;
    //生鲜客流
    private String freshKlCount;
}

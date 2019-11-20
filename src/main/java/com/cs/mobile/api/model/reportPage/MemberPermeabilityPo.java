package com.cs.mobile.api.model.reportPage;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MemberPermeabilityPo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String vipMark;

    private String onlineMark;

    private BigDecimal count;
}

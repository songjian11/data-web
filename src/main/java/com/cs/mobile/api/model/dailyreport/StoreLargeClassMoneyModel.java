package com.cs.mobile.api.model.dailyreport;

import lombok.Data;

import java.io.Serializable;

/**
 * 门店大类库存金额
 */
@Data
public class StoreLargeClassMoneyModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //大类编码
    private String dept;
    //大类名称
    private String deptName;
    //库存金额
    private String realSohAmt;
    //占比
    private String amtPercent;
    //标准周转天数
    private String standardDays;
    //实际周转天数
    private String actualDays;
    //门店编码
    private String store;
}

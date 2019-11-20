package com.cs.mobile.api.model.market;

import lombok.Data;

import java.io.Serializable;

/**
 * 品类目标值
 */
@Data
public class CategoryGoalModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //品类名称
    private String categoryName;
    //销售
    private String sale;
    //毛利额
    private String rate;
}

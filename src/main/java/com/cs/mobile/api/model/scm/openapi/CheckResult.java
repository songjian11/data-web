package com.cs.mobile.api.model.scm.openapi;

import com.cs.mobile.api.model.scm.ItemTaxRate;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单数据检查结果
 * 检查商品税率
 * 检查入库仓位编码
 *
 * @author wells.wong
 * @date 2019/9/29
 */
@Data
public class CheckResult implements Serializable {
    //税率
    private ItemTaxRate itemTaxRate;
    //入库仓位
    private String physicalWh;
}

package com.cs.mobile.api.model.scm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单行信息
 *
 * @author wells.wong
 * @date 2019年7月25日
 */
@Data
@ApiModel(value = "PoDetail", description = "订单行信息")
public class PoDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "订单号", required = true)
    private String poSn;
    @ApiModelProperty(value = "商品编码", required = true)
    private String item;
    @ApiModelProperty(value = "商品名称", required = true)
    private String itemDesc;
    @ApiModelProperty(value = "标准件", required = true)
    private BigDecimal standardOfPackage;
    @ApiModelProperty(value = "订货单位", required = true)
    private String uomDesc;
    @ApiModelProperty(value = "税率", required = true)
    private BigDecimal taxRate;
    @ApiModelProperty(value = "件数", required = true)
    private BigDecimal numberOfPackage;
    @ApiModelProperty(value = "采购数量（KG）", required = true)
    private BigDecimal poQty;
    @ApiModelProperty(value = "重量单价（元/KG）", required = true)
    private BigDecimal unitPrice;
    @ApiModelProperty(value = "件数单价（元/件）", required = true)
    private BigDecimal perPrice;
    @ApiModelProperty(value = "剩余发货数量（KG）", required = true)
    private BigDecimal surplusQty;
    @ApiModelProperty(value = "创建人ID", required = false)
    private String creatorId;
    @ApiModelProperty(value = "创建人", required = false)
    private String creator;
    @ApiModelProperty(value = "创建时间", required = false)
    private Date createTime;
    @ApiModelProperty(value = "修改时间", required = false)
    private Date updateTime;
    @ApiModelProperty(value = "备注", required = false)
    private String remark;
    @ApiModelProperty(value = "预测到仓价格（元/KG）", required = true)
    private BigDecimal predictArrivalPrice;
    @ApiModelProperty(value = "最终到仓价格（元/KG）", required = true)
    private BigDecimal finalArrivalPrice;
}

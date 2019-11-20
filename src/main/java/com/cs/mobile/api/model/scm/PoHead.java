package com.cs.mobile.api.model.scm;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单头信息
 *
 * @author wells.wong
 * @date 2019年7月25日
 */
@Data
@ApiModel(value = "PoHead", description = "订单头信息")
public class PoHead implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "订单号", required = true)
    private String poSn;
    @ApiModelProperty(value = "基地编码", required = false)
    private String supplier;
    @ApiModelProperty(value = "基地名称", required = false)
    private String supName;
    @ApiModelProperty(value = "入库仓库编码", required = false)
    private String whCode;
    @ApiModelProperty(value = "入库仓库名称", required = false)
    private String whName;
    @ApiModelProperty(value = "有效期", required = false)
    private Date validityPerioid;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "预计到货日期", required = false)
    private Date expArrivalDate;
    @ApiModelProperty(value = "采购员", required = false)
    private String purchaser;
    @ApiModelProperty(value = "单据状态 '01'未提交'02'未审核'03'未发货'04'已完结'05'作废", required = false)
    private String poStatus;
    @ApiModelProperty(value = "单据类型【单类型 '01'基地回货系统录入 '02'报价系统接入基地采购类型 '03'报价系统接入供应商采购类型】", required = false)
    private String poType;
    @ApiModelProperty(value = "备注", required = false)
    private String remark;
    @ApiModelProperty(value = "创建人ID", required = false)
    private String creatorId;
    @ApiModelProperty(value = "创建人", required = false)
    private String creator;
    @ApiModelProperty(value = "创建时间", required = false)
    private Date createTime;
    @ApiModelProperty(value = "修改时间", required = false)
    private Date updateTime;
    @ApiModelProperty(value = "审核人ID", required = false)
    private String auditorId;
    @ApiModelProperty(value = "审核人", required = false)
    private String auditor;
    @ApiModelProperty(value = "审核时间", required = false)
    private Date auditTime;
    @ApiModelProperty(value = "报价单号", required = true)
    private String biddingSn;
}

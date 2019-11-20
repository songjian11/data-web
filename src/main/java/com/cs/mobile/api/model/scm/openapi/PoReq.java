package com.cs.mobile.api.model.scm.openapi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "PoReq", description = "订单头信息请求参数对象")
public class PoReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "创建者ID", required = true)
    private String creatorId;
    @ApiModelProperty(value = "创建者姓名", required = true)
    private String creator;
    @ApiModelProperty(value = "审核者ID", required = true)
    private String auditorId;
    @ApiModelProperty(value = "审核者", required = true)
    private String auditor;
    @ApiModelProperty(value = "创建类型【02：基地（采购员为员工） 01：供应商（采购员为供应商）】,默认为02", required = false)
    private String type = "02";
    @ApiModelProperty(value = "大类【36,37】", required = false)
    private Integer deptId;
    @ApiModelProperty(value = "头信息", required = true)
    private HeadReq headReq;
    @ApiModelProperty(value = "行信息", required = true)
    private DetailReq detailReq;
    @ApiModelProperty(value = "报价单号", required = true)
    private String biddingSn;
}

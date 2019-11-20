package com.cs.mobile.api.model.scm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发货头信息
 * 
 * @author wells.wong
 * @date 2019年7月25日
 *
 */
@Data
@ApiModel(value = "PoAsnHead", description = "发货头信息")
public class PoAsnHead implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "发货单号", required = true)
	private String poAsnSn;
	@ApiModelProperty(value = "订单号", required = true)
	private String poSn;
	@ApiModelProperty(value = "运输单位", required = true)
	private String transportCompany;
	@ApiModelProperty(value = "运费", required = true)
	private BigDecimal freight;
	@ApiModelProperty(value = "总数量", required = true)
	private BigDecimal totalQty;
	@ApiModelProperty(value = "司机", required = true)
	private String driver;
	@ApiModelProperty(value = "车牌号", required = true)
	private String carNumber;
	@ApiModelProperty(value = "司机电话", required = true)
	private String drvierPhone;
	@ApiModelProperty(value = "车型", required = true)
	private String carType;
	@ApiModelProperty(value = "单据状态'01'未发货'02'已完结'05'作废", required = true)
	private String poAsnStatus;
	@ApiModelProperty(value = "创建人ID", required = true)
	private String creatorId;
	@ApiModelProperty(value = "创建人", required = true)
	private String creator;
	@ApiModelProperty(value = "创建时间", required = true)
	private Date createTime;
	@ApiModelProperty(value = "同步状态0待同步 1 已同步", required = true)
	private Integer transferStatus;

}

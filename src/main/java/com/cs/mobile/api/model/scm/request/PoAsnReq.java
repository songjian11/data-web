package com.cs.mobile.api.model.scm.request;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PoAsnReq", description = "发货请求参数对象")
public class PoAsnReq implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "发货单号", required = true)
	private String poAsnSn;
	@ApiModelProperty(value = "订单号", required = true)
	private String poSn;
	@ApiModelProperty(value = "运费", required = true)
	private BigDecimal freight;
	@ApiModelProperty(value = "司机", required = true)
	private String driver;
	@ApiModelProperty(value = "车牌号", required = true)
	private String carNumber;
	@ApiModelProperty(value = "司机电话", required = true)
	private String drvierPhone;
	@ApiModelProperty(value = "车型", required = true)
	private String carType;
	@ApiModelProperty(value = "运输单位", required = true)
	private String transportCompany;
	@ApiModelProperty(value = "发货商品及数量字符串【[{'item':'111','itemDesc':'商品1','remark':'备注1'},{'item':'222','itemDesc':'商品2','remark':'备注2'}]】", required = true)
	private String itemStr;
}

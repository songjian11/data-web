package com.cs.mobile.api.model.scm.request;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PoHeadReq", description = "订单头信息请求参数对象")
public class PoHeadReq implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "采购员", required = false)
	private String purchaser;
	@ApiModelProperty(value = "订单单号", required = false)
	private String poSn;
	@ApiModelProperty(value = "基地编码", required = false)
	private String supplier;
	@ApiModelProperty(value = "基地名称", required = false)
	private String supName;
	@ApiModelProperty(value = "入库仓库编码", required = false)
	private String whCode;
	@ApiModelProperty(value = "入库仓库名称", required = false)
	private String whName;
	@ApiModelProperty(value = "备注", required = false)
	private String remark;
	@ApiModelProperty(value = "预计到货日期", required = false)
	private String expArrivalDate;
}

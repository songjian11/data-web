package com.cs.mobile.api.model.scm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发货行信息
 * 
 * @author wells.wong
 * @date 2019年7月25日
 *
 */
@Data
@ApiModel(value = "PoAsnDetail", description = "发货行信息")
public class PoAsnDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "发货单号", required = true)
	private String poAsnSn;
	@ApiModelProperty(value = "商品编码", required = true)
	private String item;
	@ApiModelProperty(value = "商品名称", required = true)
	private String itemDesc;
	@ApiModelProperty(value = "发货数量（KG）", required = true)
	private BigDecimal poAsnQty;
	@ApiModelProperty(value = "创建人ID", required = true)
	private String creatorId;
	@ApiModelProperty(value = "创建人", required = true)
	private String creator;
	@ApiModelProperty(value = "创建时间", required = true)
	private Date createTime;
	@ApiModelProperty(value = "备注", required = false)
	private String remark;

}

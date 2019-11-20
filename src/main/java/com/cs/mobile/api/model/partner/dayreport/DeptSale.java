package com.cs.mobile.api.model.partner.dayreport;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 大类销售对象
 * 
 * @author wells
 * @date 2019年3月14日
 */
@Data
@ApiModel(value = "DeptSale", description = "大类销售对象")
public class DeptSale implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "大类ID", required = true)
	private String deptId;
	@ApiModelProperty(value = "大类名称", required = true)
	private String deptName;
	@ApiModelProperty(value = "当前值", required = true)
	private BigDecimal curValue;
	@ApiModelProperty(value = "历史值", required = true)
	private BigDecimal historyValue;

	public int getCompare() {
		return Integer.parseInt(this.deptId);
	}

}

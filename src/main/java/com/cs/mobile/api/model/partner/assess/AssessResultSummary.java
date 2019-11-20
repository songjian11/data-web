package com.cs.mobile.api.model.partner.assess;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.api.model.partner.progress.CostVal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 考核结果汇总对象
 * 
 * @author wells
 * @date 2019年4月3日
 */
@Data
@ApiModel(value = "AssessResultSummary", description = "考核结果汇总对象")
public class AssessResultSummary implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "销售收入", required = false)
	private BigDecimal saleTotal;
	@ApiModelProperty(value = "销售毛利额", required = false)
	private BigDecimal frontGpTotal;
	@ApiModelProperty(value = "后台", required = false)
	private BigDecimal afterGpTotal;
	@ApiModelProperty(value = "招商收入", required = false)
	private BigDecimal attractTotal;
	@ApiModelProperty(value = "DC成本", required = false)
	private BigDecimal dcTotal;
	@ApiModelProperty(value = "费用目标列表", required = false)
	private CostVal costValObject;
	@ApiModelProperty(value = "总费用", required = false)
	private BigDecimal costTotal;
	@ApiModelProperty(value = "库存", required = false)
	private BigDecimal stock;

	public AssessResultSummary(CostVal costValObject, BigDecimal saleTotal, BigDecimal frontGpTotal,
			BigDecimal afterGpTotal, BigDecimal attractTotal, BigDecimal dcTotal, BigDecimal costTotal,
			BigDecimal stock) {
		super();
		this.costValObject = costValObject;
		this.saleTotal = saleTotal;
		this.frontGpTotal = frontGpTotal;
		this.afterGpTotal = afterGpTotal;
		this.attractTotal = attractTotal;
		this.dcTotal = dcTotal;
		this.costTotal = costTotal;
		this.stock = stock;
	}

}

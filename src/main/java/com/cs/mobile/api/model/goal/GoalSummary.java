package com.cs.mobile.api.model.goal;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.api.model.partner.progress.CostVal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 目标汇总对象
 * 
 * @author wells
 * @time 2018年12月17日
 */
@Data
@ApiModel(value = "GoalSummary", description = "目标汇总对象")
public class GoalSummary implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "费用目标列表", required = false)
	private CostVal costValObject;
	@ApiModelProperty(value = "销售目标", required = false)
	private BigDecimal saleTotalGoal;
	@ApiModelProperty(value = "毛利额目标", required = false)
	private BigDecimal frontGpTotalGoal;
	@ApiModelProperty(value = "后台收入目标", required = false)
	private BigDecimal afterGpTotalGoal;
	@ApiModelProperty(value = "招商收入目标", required = false)
	private BigDecimal attractTotalGoal;
	@ApiModelProperty(value = "DC成本", required = false)
	private BigDecimal dcTotalGoal;
	@ApiModelProperty(value = "总费用目标", required = false)
	private BigDecimal costTotalGoal;

	public GoalSummary(CostVal costValObject, BigDecimal saleTotalGoal, BigDecimal frontGpTotalGoal,
			BigDecimal afterGpTotalGoal, BigDecimal attractTotalGoal, BigDecimal dcTotalGoal,
			BigDecimal costTotalGoal) {
		super();
		this.costValObject = costValObject;
		this.saleTotalGoal = saleTotalGoal;
		this.frontGpTotalGoal = frontGpTotalGoal;
		this.afterGpTotalGoal = afterGpTotalGoal;
		this.attractTotalGoal = attractTotalGoal;
		this.dcTotalGoal = dcTotalGoal;
		this.costTotalGoal = costTotalGoal;
	}
}

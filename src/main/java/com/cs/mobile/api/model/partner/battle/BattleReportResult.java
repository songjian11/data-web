package com.cs.mobile.api.model.partner.battle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.cs.mobile.common.utils.OperationUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BattleResultReport", description = "战报对象")
public class BattleReportResult implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "时间轴列表数据总和", required = false)
	private BigDecimal timeLineReportTotal;
	@ApiModelProperty(value = "时间轴列表数据总和单位", required = false)
	private String timeLineReportTotalUnit;
	@ApiModelProperty(value = "时间轴列表数据", required = false)
	private List<TimeLineReport> timeLineReportList;
	@ApiModelProperty(value = "去年总额", required = false)
	private BigDecimal lastYearTotal;
	@ApiModelProperty(value = "去年可比总额", required = false)
	private BigDecimal lastYearCTotal;
	@ApiModelProperty(value = "上个月总额", required = false)
	private BigDecimal lastMonthTotal;
	@ApiModelProperty(value = "当前总额", required = false)
	private BigDecimal curTotal;
	@ApiModelProperty(value = "当前总目标", required = false)
	private BigDecimal curTotalGoal;
	@ApiModelProperty(value = "当前可比总额", required = false)
	private BigDecimal curCTotal;
	@ApiModelProperty(value = "比率=进度值/目标值", required = false)
	private BigDecimal processRatio;
	@ApiModelProperty(value = "同比", required = false)
	private BigDecimal yearRatio;
	@ApiModelProperty(value = "环比", required = false)
	private BigDecimal monthRatio;
	@ApiModelProperty(value = "可比", required = false)
	private BigDecimal availableRatio;
	@ApiModelProperty(value = "排名列表数据", required = false)
	private List<RankReport> rankReportList;

	public BigDecimal getProcessRatio() {
		return OperationUtil.divideHandler(curTotal, curTotalGoal).multiply(new BigDecimal(100));
	}
}

package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.common.utils.OperationUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ProgressReport", description = "首页进度报表对象")
public class ProgressReport implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "类别名称", required = false)
	private String typeName;

	@ApiModelProperty(value = "实际额", required = false)
	private BigDecimal actualVal;

	@ApiModelProperty(value = "当前目标额", required = false)
	private BigDecimal goalVal;

	@ApiModelProperty(value = "当期目标额占比", required = false)
	private BigDecimal goalRatio;

	@ApiModelProperty(value = "总目标额", required = false)
	private BigDecimal goalCountVal;

	@ApiModelProperty(value = "差异额", required = false)
	private BigDecimal diffVal;

	@ApiModelProperty(value = "实际占比", required = false)
	private BigDecimal actualRatio;

	@ApiModelProperty(value = "差异占比", required = false)
	private BigDecimal diffRatio;

	@ApiModelProperty(value = "单位", required = false)
	private String unit = "万";

	public ProgressReport(String typeName, BigDecimal actualVal, BigDecimal goalVal) {
		this.typeName = typeName;
		this.actualVal = actualVal;
		this.goalVal = goalVal;
	}

	public ProgressReport() {
	}

	public static class Builder {
		private String typeName;
		private BigDecimal actualVal;
		private BigDecimal goalVal;
		private BigDecimal goalRatio;
		private BigDecimal goalCountVal;
		private BigDecimal diffVal;
		private BigDecimal actualRatio;
		private BigDecimal diffRatio;
		private String unit;

		public Builder typeName(String typeName) {
			this.typeName = typeName;
			return this;
		}

		public Builder actualVal(BigDecimal actualVal) {
			this.actualVal = actualVal;
			return this;
		}

		public Builder goalVal(BigDecimal goalVal) {
			this.goalVal = goalVal;
			return this;
		}

		public Builder goalRatio(BigDecimal goalRatio) {
			this.goalRatio = goalRatio;
			return this;
		}

		public Builder goalCountVal(BigDecimal goalCountVal) {
			this.goalCountVal = goalCountVal;
			return this;
		}

		public Builder diffVal(BigDecimal diffVal) {
			this.diffVal = diffVal;
			return this;
		}

		public Builder actualRatio(BigDecimal actualRatio) {
			this.actualRatio = actualRatio;
			return this;
		}

		public Builder diffRatio(BigDecimal diffRatio) {
			this.diffRatio = diffRatio;
			return this;
		}

		public Builder unit(String unit) {
			this.unit = unit;
			return this;
		}

		/**
		 * 计算历史月份数据的比例
		 * 
		 * @return
		 * @author wells
		 * @date 2019年4月29日
		 */
		public Builder ratio(BigDecimal goalCountVal, BigDecimal actualVal) {
			this.goalRatio = new BigDecimal(100);
			if (goalCountVal.compareTo(BigDecimal.ZERO) == -1) {// 目标小于0
				// 2-实际完成数/目标完成数
				this.actualRatio = new BigDecimal(2).subtract(OperationUtil.divideHandler(actualVal, goalCountVal))
						.multiply(new BigDecimal(100));
				this.diffRatio = this.actualRatio.subtract(this.goalRatio);
			} else {// 目标大于0
				this.actualRatio = OperationUtil.divideHandler(actualVal, goalCountVal).multiply(new BigDecimal(100));
				this.diffRatio = OperationUtil.divideHandler(actualVal.subtract(goalCountVal), goalCountVal).multiply(new BigDecimal(100));
			}
			return this;
		}

		public ProgressReport build() {
			return new ProgressReport(this);
		}

	}

	private ProgressReport(Builder builder) {
		typeName = builder.typeName;
		actualVal = builder.actualVal;
		goalVal = builder.goalVal;
		goalRatio = builder.goalRatio;
		goalCountVal = builder.goalCountVal;
		diffVal = builder.diffVal;
		actualRatio = builder.actualRatio;
		diffRatio = builder.diffRatio;
		unit = builder.unit;
	}

}

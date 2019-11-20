package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.common.utils.OperationUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CostVal", description = "各项成本")
public class CostVal implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "人力", required = false)
	private BigDecimal manpower = BigDecimal.ZERO;

	@ApiModelProperty(value = "折旧", required = false)
	private BigDecimal depreciation = BigDecimal.ZERO;

	@ApiModelProperty(value = "水电", required = false)
	private BigDecimal hydropower = BigDecimal.ZERO;

	@ApiModelProperty(value = "租赁", required = false)
	private BigDecimal lease = BigDecimal.ZERO;

	@ApiModelProperty(value = "其他", required = false)
	private BigDecimal other = BigDecimal.ZERO;

	public CostVal unitConvert(BigDecimal divisor) {
		this.manpower = OperationUtil.divideHandler(this.manpower, divisor);
		this.depreciation = OperationUtil.divideHandler(this.depreciation, divisor);
		this.hydropower = OperationUtil.divideHandler(this.hydropower, divisor);
		this.lease = OperationUtil.divideHandler(this.lease, divisor);
		this.other = OperationUtil.divideHandler(this.other, divisor);
		return this;
	}

	/**
	 * 当前累计各项目标
	 * 
	 * @author wells
	 * @return
	 * @time 2018年12月20日
	 */
	public CostVal getCurCost(int totalDays, int curDays) {
		CostVal curCostVal = new CostVal();// 不能改变原有对象，因而这里重新新建一个对象
		curCostVal.setDepreciation(OperationUtil.divideHandler(this.getDepreciation(), new BigDecimal(totalDays))
				.multiply(new BigDecimal(curDays)));
		curCostVal.setHydropower(OperationUtil.divideHandler(this.getHydropower(), new BigDecimal(totalDays))
				.multiply(new BigDecimal(curDays)));
		curCostVal.setLease(OperationUtil.divideHandler(this.getLease(), new BigDecimal(totalDays))
				.multiply(new BigDecimal(curDays)));
		curCostVal.setManpower(OperationUtil.divideHandler(this.getManpower(), new BigDecimal(totalDays))
				.multiply(new BigDecimal(curDays)));
		curCostVal.setOther(OperationUtil.divideHandler(this.getOther(), new BigDecimal(totalDays))
				.multiply(new BigDecimal(curDays)));
		return curCostVal;
	}

	public void addManpower(BigDecimal item) {
		this.manpower = this.manpower.add(item);
	}

	public void addDepreciation(BigDecimal item) {
		this.depreciation = this.depreciation.add(item);
	}

	public void addHydropower(BigDecimal item) {
		this.hydropower = this.hydropower.add(item);
	}

	public void addLease(BigDecimal item) {
		this.lease = this.lease.add(item);
	}

	public void addOther(BigDecimal item) {
		this.other = this.other.add(item);
	}

	public void add(CostVal costVal) {
		this.manpower = this.manpower.add(costVal.getManpower());
		this.depreciation = this.depreciation.add(costVal.getDepreciation());
		this.hydropower = this.hydropower.add(costVal.getHydropower());
		this.lease = this.lease.add(costVal.getLease());
		this.other = this.other.add(costVal.getOther());
	}

}

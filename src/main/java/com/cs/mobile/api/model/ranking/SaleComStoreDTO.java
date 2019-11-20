package com.cs.mobile.api.model.ranking;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 销售排名返回对象
 * 
 * @author jiangliang
 * @date 2019年3月30日
 */
@Data
@ApiModel(value = "SaleComStoreDTO", description = "店群-小店返回对象")
public class SaleComStoreDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "序号", required = true)
	private Integer index;

	@ApiModelProperty(value = "店群ID", required = true)
	private String groupId;

	@ApiModelProperty(value = "店群名称", required = true)
	private String groupName;

	@ApiModelProperty(value = "大店ID", required = true)
	private String storeId;

	@ApiModelProperty(value = "大店名称", required = true)
	private String storeName;

	@ApiModelProperty(value = "小店ID", required = true)
	private String comId;

	@ApiModelProperty(value = "小店名称", required = true)
	private String comName;

	@ApiModelProperty(value = "点赞数", required = true)
	private Integer giveNum;

	@ApiModelProperty(value = "是否点赞", required = true)
	private String ifGive;

	@ApiModelProperty(value = "店长", required = true)
	private String storeManager;

	@ApiModelProperty(value = "单位", required = true)
	private String unit = "万";

	@ApiModelProperty(value = "实际销售", required = true)
	private BigDecimal saleActualValue;

	@ApiModelProperty(value = "目标销售", required = true)
	private BigDecimal saleGoalValue;

	@ApiModelProperty(value = "达成率", required = true)
	private BigDecimal reachRate;

	@ApiModelProperty(value = "增长率", required = true)
	private BigDecimal increaseRate;

	@ApiModelProperty(value = "去年同比销售", required = true)
	private BigDecimal saleUpValue;

	public BigDecimal getSaleActualValue() {
		return null == saleActualValue ? BigDecimal.ZERO : saleActualValue.divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getSaleGoalValue() {
		return null == saleGoalValue ? BigDecimal.ZERO : saleGoalValue.divide(new BigDecimal(10000),2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getReachRate() {
		return null == reachRate ? BigDecimal.ZERO : reachRate.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getIncreaseRate() {
		return null == increaseRate ? BigDecimal.ZERO : increaseRate.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
}

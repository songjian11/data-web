package com.cs.mobile.api.model.partner.assess;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 小店奖金
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "ComBonus", description = "小店奖金对象")
public class ComBonus implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = true)
	private String ym;
	@ApiModelProperty(value = "门店编码", required = true)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = true)
	private String storeName;
	@ApiModelProperty(value = "小店编码", required = true)
	private String comId;
	@ApiModelProperty(value = "小店名称", required = true)
	private String comName;
	@ApiModelProperty(value = "小店店长工号", required = true)
	private String managerId;
	@ApiModelProperty(value = "小店店长名称", required = true)
	private String managerName;
	@ApiModelProperty(value = "目标利润", required = true)
	private BigDecimal goalProfit;
	@ApiModelProperty(value = "实际利润", required = true)
	private BigDecimal actualProfit;
	@ApiModelProperty(value = "目标销售", required = true)
	private BigDecimal goalSale;
	@ApiModelProperty(value = "销售", required = true)
	private BigDecimal sale;
	@ApiModelProperty(value = "招商收入", required = true)
	private BigDecimal attract;
	@ApiModelProperty(value = "费用", required = true)
	private BigDecimal cost;
	@ApiModelProperty(value = "前台毛利额", required = true)
	private BigDecimal frontGp;
	@ApiModelProperty(value = "奖金金额", required = true)
	private BigDecimal bonus;
	@ApiModelProperty(value = "数据状态", required = true)
	private Integer dataStatus;
	@ApiModelProperty(value = "审核状态", required = true)
	private Integer auditStatus;
	@ApiModelProperty(value = "更新时间", required = true)
	private String updateTime;
}

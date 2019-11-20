package com.cs.mobile.api.model.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 持久类用户
 * 
 * @author jiangliang
 * @date 2018年11月19日
 */
@Data
@ApiModel(value = "人员信息", description = "店铺人员")
public class StorePerson implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "工号", required = false)
	private String personId;

	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;

	@ApiModelProperty(value = "门店名称", required = false)
	private String storeName;

	@ApiModelProperty(value = "小店编码", required = false)
	private String comId;

	@ApiModelProperty(value = "小店名称", required = false)
	private String comName;

	@ApiModelProperty(value = "岗位编码", required = false)
	private String positionId;

	@ApiModelProperty(value = "岗位系数", required = false)
	private Integer positionVal;

	@ApiModelProperty(value = "岗位名称", required = false)
	private String positionName;

	@ApiModelProperty(value = "姓名", required = false)
	private String name;

	@ApiModelProperty(value = "性别", required = false)
	private Integer gender;

	@ApiModelProperty(value = "入司时间", required = false)
	private Date lastInDate;

	@ApiModelProperty(value = "每月出勤天数", required = false)
	private Integer attendance;

	@ApiModelProperty(value = "分配金额调整前", required = false)
	private BigDecimal beforMoney;

	@ApiModelProperty(value = "分配金额调整后", required = false)
	private BigDecimal afterAmount;

	@ApiModelProperty(value = "调整月份", required = false)
	private String changeYm;

	@ApiModelProperty(value = "审批状态", required = false)
	private Integer status;

	@ApiModelProperty(value = "调整说明", required = false)
	private String adjustDesc;

	@ApiModelProperty(value = "是否可调整金额", required = false)
	private String isAdjust = "Y";

	@ApiModelProperty(value = "单位", required = false)
	private String unit = "元";

	public BigDecimal getBeforMoney() {
		return null == beforMoney ? null : beforMoney.setScale(0,BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getAfterAmount() {
		return null == afterAmount ? null : afterAmount.setScale(0,BigDecimal.ROUND_HALF_DOWN);
	}
}

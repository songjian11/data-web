package com.cs.mobile.api.model.partner.assess.response;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cs.mobile.common.constant.PersonBonusStatusEnum;
import com.cs.mobile.common.constant.PositionEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 人员奖金明细
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "PersonBonusItemResp", description = "人员奖金明细对象")
public class PersonBonusItemResp implements Serializable {
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
	@ApiModelProperty(value = "工号", required = true)
	private String personId;
	@ApiModelProperty(value = "姓名", required = true)
	private String personName;
	@ApiModelProperty(value = "岗位", required = true)
	private Integer positionId;
	@ApiModelProperty(value = "岗位名称", required = true)
	private String positionName;
	@ApiModelProperty(value = "调整前金额", required = true)
	private BigDecimal beforMoney;
	@ApiModelProperty(value = "调整后金额", required = true)
	private BigDecimal afterMoney;
	@ApiModelProperty(value = "调整说明", required = true)
	private String adjustDesc;
	@ApiModelProperty(value = "状态0：等待提交；1：等待审核；2：审核通过；3：审核不通过", required = true)
	private Integer status;
	@ApiModelProperty(value = "状态名称", required = true)
	private String statusName;

	public String getPositionName() {
		if (this.positionId != null) {
			return PositionEnum.getName(this.positionId);
		} else {
			return null;
		}

	}

	public String getStatusName() {
		if (this.status != null) {
			return PersonBonusStatusEnum.getName(this.status);
		} else {
			return null;
		}

	}
}

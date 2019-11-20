package com.cs.mobile.api.model.partner.assess.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.cs.mobile.common.constant.PersonBonusStatusEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 小店人员奖金
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "ComPersonBonusResp", description = "小店人员奖金对象")
public class ComPersonBonusResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "未分配奖金", required = true)
	private BigDecimal unDistrBonus;
	@ApiModelProperty(value = "人员奖金列表", required = true)
	private List<PersonBonusItemResp> personBonusList;
	@ApiModelProperty(value = "状态 0：等待提交；1：等待审核；2：审核通过；3：审核不通过", required = true)
	private Integer status;
	@ApiModelProperty(value = "状态名称", required = true)
	private String statusName;
	@ApiModelProperty(value = "总奖金", required = true)
	private BigDecimal totalBonus;
	@ApiModelProperty(value = "可调总奖金的比例 ex:20%则该值为0.2", required = true)
	private BigDecimal modifyRatio = new BigDecimal(0.2);

	public String getStatusName() {
		if (this.status != null) {
			return PersonBonusStatusEnum.getName(this.status);
		} else {
			return null;
		}
	}
}

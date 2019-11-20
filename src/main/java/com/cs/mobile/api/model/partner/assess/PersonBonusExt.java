package com.cs.mobile.api.model.partner.assess;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 人员奖金明细扩展表</br>
 * 包含小店信息</br>
 * 方便按年月及小店分组</br>
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "PersonBonusExt", description = "人员奖金明细扩展对象")
public class PersonBonusExt extends ComBonus implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "工号", required = true)
	private String personId;
	@ApiModelProperty(value = "姓名", required = true)
	private String personName;
	@ApiModelProperty(value = "岗位编码", required = true)
	private Integer positionId;
	@ApiModelProperty(value = "调整前金额", required = true)
	private BigDecimal beforMoney;
	@ApiModelProperty(value = "调整后金额", required = true)
	private BigDecimal afterMoney;
	@ApiModelProperty(value = "调整说明", required = true)
	private String adjustDesc;
	@ApiModelProperty(value = "状态：0：等待提交；1：等待审核；2：审核通过；3：审核不通过", required = true)
	private Integer status;

}

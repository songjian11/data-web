package com.cs.mobile.api.model.partner.assess;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 人员奖金明细表
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "PersonBonus", description = "人员奖金明细对象")
public class PersonBonus implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = true)
	private String ym;
	@ApiModelProperty(value = "工号", required = true)
	private String personId;
	@ApiModelProperty(value = "姓名", required = true)
	private String personName;
	@ApiModelProperty(value = "门店编码", required = true)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = true)
	private String storeName;
	@ApiModelProperty(value = "小店编码", required = true)
	private String comId;
	@ApiModelProperty(value = "小店名称", required = true)
	private String comName;
	@ApiModelProperty(value = "岗位编码", required = true)
	private String positionId;
	@ApiModelProperty(value = "调整前金额", required = true)
	private BigDecimal beforMoney;
	@ApiModelProperty(value = "调整后金额", required = true)
	private BigDecimal afterMoney;
	@ApiModelProperty(value = "调整说明", required = true)
	private String adjustDesc;
	@ApiModelProperty(value = "状态：0：等待提交；1：等待审核；2：审核通过；3：审核不通过", required = true)
	private Integer status;

	public PersonBonus(String ym, String personId, String personName, String storeId, String storeName, String comId,
			String comName, String positionId, BigDecimal beforMoney, BigDecimal afterMoney, String adjustDesc,
			Integer status) {
		super();
		this.ym = ym;
		this.personId = personId;
		this.personName = personName;
		this.storeId = storeId;
		this.storeName = storeName;
		this.comId = comId;
		this.comName = comName;
		this.positionId = positionId;
		this.beforMoney = beforMoney;
		this.afterMoney = afterMoney;
		this.adjustDesc = adjustDesc;
		this.status = status;
	}

}

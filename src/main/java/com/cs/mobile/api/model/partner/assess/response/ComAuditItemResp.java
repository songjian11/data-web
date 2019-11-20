package com.cs.mobile.api.model.partner.assess.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.cs.mobile.common.constant.PersonBonusStatusEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 小店审核明细
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "ComAuditItemResp", description = "小店审核明细")
public class ComAuditItemResp implements Serializable {
	private static final long serialVersionUID = 1L;
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
	@ApiModelProperty(value = "小店店长姓名", required = true)
	private String managerName;
	@ApiModelProperty(value = "奖金", required = true)
	private BigDecimal bonus;
	@ApiModelProperty(value = "状态 0：等待提交；1：等待审核；2：审核通过；3：审核不通过", required = true)
	private Integer auditStatus;
	@ApiModelProperty(value = "状态名称", required = true)
	private String auditStatusName;
	@ApiModelProperty(value = "是否需要分配 0：该小店拥有小店店长，不需要大店店长来分配 1：该小店没有小店店长，需要大店店长来分配", required = true)
	private boolean distribute;
	@ApiModelProperty(value = "人员奖金列表", required = true)
	private List<PersonBonusItemResp> personBonusList;

	/**
	 * 是否需要分配</br>
	 * 如果没有小店店长则需要大店店长来分配
	 * 
	 * @return
	 * @author wells
	 * @date 2019年4月3日
	 */
	public boolean isDistribute() {
		if (StringUtils.isEmpty(managerId)) {
			return true;
		} else {
			return false;
		}
	}

	public String getAuditStatusName() {
		if (this.auditStatus != null) {
			return PersonBonusStatusEnum.getName(this.auditStatus);
		} else {
			return null;
		}
	}
}

package com.cs.mobile.api.model.user;

import java.io.Serializable;
import java.util.List;

import com.cs.mobile.api.model.partner.PartnerUserInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息持久类
 * 
 * @author wells.wong
 * @date 2018年11月19日
 */
@Data
@ApiModel(value = "用户信息", description = "用户用户信息对象")
public class UserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "工号", required = false)
	private String personId;
	@ApiModelProperty(value = "姓名", required = false)
	private String name;
	@ApiModelProperty(value = "类型数组", required = false)
	private List<Integer> typeList;
	@ApiModelProperty(value = " 合伙人用户信息", required = false)
	private PartnerUserInfo partnerUserInfo;
	@ApiModelProperty(value = "首页类型1：合伙人首页；2：报表首页；3：生鲜专题；4:基地回货 5：市场专属报表 6:报表中心", required = false)
	private Integer indexType;
}

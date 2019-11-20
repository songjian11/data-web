package com.cs.mobile.api.model.partner;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 合伙人人员
 * 
 * @author wells
 * @date 2019年4月3日
 */
@Data
@ApiModel(value = "PartnerPerson", description = "合伙人人员")
public class PartnerPerson implements Serializable {

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
	@ApiModelProperty(value = "职位编码", required = false)
	private String positionId;
	@ApiModelProperty(value = "职位名称", required = false)
	private String positionName;
	@ApiModelProperty(value = "姓名", required = false)
	private String name;
	@ApiModelProperty(value = "姓名", required = false)
	private String gender;
	@ApiModelProperty(value = "入司时间", required = false)
	private String lastInDate;

	public String getSerialNo() {
		return this.storeId + "_" + this.comId;
	}

}

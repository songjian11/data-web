package com.cs.mobile.api.model.partner.assess.response;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 门店审核
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "StoreAuditResp", description = "门店审核")
public class StoreAuditResp implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = true)
	private String ym;
	@ApiModelProperty(value = "门店编码", required = true)
	private String storeId;
	@ApiModelProperty(value = "门店名称", required = true)
	private String storeName;
	@ApiModelProperty(value = "小店列表", required = true)
	List<ComAuditItemResp> comAuditList;

}

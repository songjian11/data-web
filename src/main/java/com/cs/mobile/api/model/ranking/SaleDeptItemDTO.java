package com.cs.mobile.api.model.ranking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 大类-品类返回对象
 * 
 * @author jiangliang
 * @date 2019年4月01日
 */
@Data
@ApiModel(value = "SaleDeptDTO", description = "大类返回对象")
public class SaleDeptItemDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "大类ID", required = true)
	private String deptId;

	@ApiModelProperty(value = "大类名称", required = true)
	private String deptName;

	@ApiModelProperty(value = "单品列表", required = true)
	private List<SaleItemDTO> itemList;

}

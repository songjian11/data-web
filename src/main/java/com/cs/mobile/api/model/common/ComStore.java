package com.cs.mobile.api.model.common;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 小店信息持久类
 * 
 * @author jiangliang
 * @date 2018年11月19日
 */
@Data
@ApiModel(value = "小店信息", description = "小店信息")
public class ComStore implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "门店编码", required = false)
	private String storeId;

	@ApiModelProperty(value = "小店编码", required = false)
	private String comId;

	@ApiModelProperty(value = "小店名称", required = false)
	private String comName;

	@ApiModelProperty(value = "大类编码", required = false)
	private String deptId;

	@ApiModelProperty(value = "人数配置", required = false)
	private Integer personTotal;

}

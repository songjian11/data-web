package com.cs.mobile.api.model.scm;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 入库仓库
 * 
 * @author wells.wong
 * @date 2019年7月25日
 *
 */
@Data
@ApiModel(value = "PoWareHouse", description = "入库仓库")
public class PoWareHouse implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "入库仓库编码", required = true)
	private String whCode;
	@ApiModelProperty(value = "入库仓库名称", required = true)
	private String whName;

}

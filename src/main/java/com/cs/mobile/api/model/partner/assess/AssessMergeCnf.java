package com.cs.mobile.api.model.partner.assess;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 考核合并配置表</br>
 * 一般是春节才有的情况</br>
 * 比如1月跟2月合并到2月考核
 * 
 * @author wells
 * @date 2019年3月27日
 */
@Data
@ApiModel(value = "AssessMergeCnf", description = "考核合并配置对象")
public class AssessMergeCnf implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "省份", required = true)
	private String provinceId;
	@ApiModelProperty(value = "主年月", required = true)
	private String mainYm;
	@ApiModelProperty(value = "被合并年月", required = true)
	private String mergeYm;
}

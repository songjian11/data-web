package com.cs.mobile.api.model.reportPage;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ReportCommonParam", description = "报表通用参数")
public class ReportCommonParam implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "省份id", required = false)
	private String provinceId;
	@ApiModelProperty(value = "区域id", required = false)
	private String areaId;
	@ApiModelProperty(value = "门店id", required = false)
	private String storeId;
	@ApiModelProperty(value = "品类", required = false)
	private String category;
	@ApiModelProperty(value = "大类id", required = false)
	private String deptId;
	@ApiModelProperty(value = "中类id", required = false)
	private String classId;
	@ApiModelProperty(value = "小类id", required = false)
	private String subClassId;
	@ApiModelProperty(value = "商品id", required = false)
	private String ssubClassId;
	@ApiModelProperty(value = "是否含税(0-否，1-是)", required = false)
	private int taxType = 1;
	
	@ApiModelProperty(value = "开始时间", required = false)
	private String beginDate;
	@ApiModelProperty(value = "结束时间", required = false)
	private String endDate;
	@ApiModelProperty(value = "商品编码", required = false)
	private String item;
	
	
	@ApiModelProperty(value = "组织编码", required = false)
	private String orgId;
	
	//用户ID
	@JsonIgnore
	private String personId;
}

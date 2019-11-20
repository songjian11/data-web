package com.cs.mobile.api.model.partner.progress;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 大类库存表
 * 
 * @author wells
 * @time 2018年12月18日
 */
@Data
@ApiModel(value = "DeptStock", description = "大类库存表对象")
public class DeptStock implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "年月", required = false)
	private String ym;
	@ApiModelProperty(value = "门店ID", required = false)
	private String storeId;
	@ApiModelProperty(value = "小店ID", required = false)
	private String comId;
	@ApiModelProperty(value = "大类ID", required = false)
	private String deptId;
	@ApiModelProperty(value = "库存金额", required = false)
	private BigDecimal stockMoney;

}

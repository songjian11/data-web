package com.cs.mobile.api.model.goods;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 门店数据源配置
 * 
 * @author wells
 * @date 2019年2月27日
 */
@Data
@ApiModel(value = "GoodsDataSourceConfig", description = "门店数据源配置")
public class GoodsDataSourceConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "业态", required = true)
	private String chain;
	@ApiModelProperty(value = "门店ID", required = true)
	private String store;
	@ApiModelProperty(value = "IP地址", required = true)
	private String host;
	@ApiModelProperty(value = "端口", required = true)
	private String port;
	@ApiModelProperty(value = "服务名", required = true)
	private String sid;
}

package com.cs.mobile.api.config;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 设置支持跨域的域名
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
@Data
@ConfigurationProperties(prefix = "cross")
public class ApiProperties {
	private String domain;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}

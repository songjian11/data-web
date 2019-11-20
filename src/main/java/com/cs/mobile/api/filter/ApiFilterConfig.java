package com.cs.mobile.api.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.cs.mobile.api.config.ApiProperties;

/**
 * cors跨域支持
 * 
 * @author wells.wong
 * @date 2018年11月18日
 */
@Configuration
@EnableConfigurationProperties(ApiProperties.class)
public class ApiFilterConfig {

	@Autowired
	private ApiProperties properties;

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		String[] domains = {};
		if (!StringUtils.isEmpty(properties.getDomain())) {
			domains = properties.getDomain().split(",");
		}
		for (String domain : domains) {
			config.addAllowedOrigin(domain);
		}
		config.setAllowCredentials(true);
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");
		UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
		configSource.registerCorsConfiguration("/api/**", config);
		return new CorsFilter(configSource);
	}
}

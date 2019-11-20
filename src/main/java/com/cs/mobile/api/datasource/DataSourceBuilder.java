package com.cs.mobile.api.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.cs.mobile.api.model.goods.GoodsDataSourceConfig;

import lombok.Data;

@Data
public class DataSourceBuilder {
	private static final String URL_FORMATTER = "jdbc:oracle:thin:@%s:%s:%s";
	private static final String SERVICE_NAME_URL_FORMATTER = "jdbc:oracle:thin:@%s:%s/%s";
	private static final String DS_KEY_FORMATTER = "ds_%s";
	private String dsKey;
	private DruidDataSource dataSource;

	public DataSourceBuilder(GoodsDataSourceConfig dataSourceConfig, String userName, String password,
			DruidProperties druidProperties) {
		DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
		if (dataSourceConfig.getHost().startsWith("//")) {
			dataSource.setUrl(String.format(SERVICE_NAME_URL_FORMATTER, dataSourceConfig.getHost(),
					dataSourceConfig.getPort(), dataSourceConfig.getSid()));
		} else {
			dataSource.setUrl(String.format(URL_FORMATTER, dataSourceConfig.getHost(), dataSourceConfig.getPort(),
					dataSourceConfig.getSid()));
		}
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		this.dataSource = druidProperties.dataSource(dataSource);
		this.dsKey = String.format(DS_KEY_FORMATTER, dataSourceConfig.getStore());
	}

}

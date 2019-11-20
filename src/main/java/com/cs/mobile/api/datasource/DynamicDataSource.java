package com.cs.mobile.api.datasource;

import java.lang.reflect.Field;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * 动态数据源
 * 
 * @author wells
 * @date 2019年2月26日
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
	public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
		super.setDefaultTargetDataSource(defaultTargetDataSource);
		super.setTargetDataSources(targetDataSources);
		super.afterPropertiesSet();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		try {
			DataSourceBuilder dataSourceBuilder = DataSourceHolder.getDataSource();
			if (dataSourceBuilder != null) {
				Map<Object, Object> map = getTargetDataSources();
				if (!map.containsKey(dataSourceBuilder.getDsKey())) {
					map.put(dataSourceBuilder.getDsKey(), dataSourceBuilder.getDataSource());
					super.afterPropertiesSet();
				}
				log.debug("dataSourceBuilder存在，determineCurrentLookupKey返回：" + dataSourceBuilder.getDsKey());
				return dataSourceBuilder.getDsKey();
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			log.error("======determineCurrentLookupKey出现异常:", e);
		}
		log.debug("======determineCurrentLookupKey返回：null");
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> getTargetDataSources() throws NoSuchFieldException, IllegalAccessException {
		Field field = AbstractRoutingDataSource.class.getDeclaredField("targetDataSources");
		field.setAccessible(true);
		return (Map<Object, Object>) field.get(this);
	}
}
package com.cs.mobile.api.datasource;

public final class DataSourceHolder {
	private static ThreadLocal<DataSourceBuilder> threadLocal = new ThreadLocal<DataSourceBuilder>() {
		@Override
		protected DataSourceBuilder initialValue() {
			return null;
		}
	};

	public static DataSourceBuilder getDataSource() {
		return threadLocal.get();
	}

	public static void setDataSource(DataSourceBuilder dataSourceBuilder) {
		threadLocal.set(dataSourceBuilder);
	}

	public static void clearDataSource() {
		threadLocal.remove();
	}
}

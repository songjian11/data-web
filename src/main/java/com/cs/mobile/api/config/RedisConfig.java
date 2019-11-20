package com.cs.mobile.api.config;

import com.alibaba.druid.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis配置
 * 
 * @author wells.wong
 * @date 2018年11月25日
 */
@Configuration
@Slf4j
@Data
public class RedisConfig {
	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.port}")
	private int port;
	@Value("${spring.redis.password:none}")
	private String password;
	@Value("${spring.redis.timeout}")
	private int timeout;

	@Bean
	public JedisPoolConfig getRedisConfig() {
		return new JedisPoolConfig();
	}

	@Bean
	public JedisPool getJedisPool() {
		JedisPoolConfig config = getRedisConfig();
		log.info("init Jredis Pool ...");
		if (StringUtils.isEmpty(password) || "none".equalsIgnoreCase(password)) {
			return new JedisPool(config, host, port, timeout);
		} else {
			return new JedisPool(config, host, port, timeout, password);
		}
	}
}
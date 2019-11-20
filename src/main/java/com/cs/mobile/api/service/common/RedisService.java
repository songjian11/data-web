package com.cs.mobile.api.service.common;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * redis服务接口
 * 
 * @author wells.wong
 * @date 2018年11月25日
 */
public interface RedisService {
	Jedis getResource();

	void set(String key, String value);

	String get(String key);

	void lpush(String key, String... values);

	String lpop(String key);

	Long llen(String key);

	void sadd(String key, String... values);

	Set<String> smembers(String key);

	Boolean sismembers(String key, String member);

	void smove(String oldKey, String newKey, String value);

	Object eval(String script, int keyCnt, String... params);

	Long del(String format);

	void setObject(String key, Object value, int expireTime);

	Object getObject(String key);

	/**
	 * 获取分布式锁，过期时间为毫秒
	 */
	boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime);

	/**
	 * 释放分布式锁
	 */
	boolean releaseDistributedLock(String lockKey, String requestId);

	Long hset(String key, String filed, String value);

	List<String> hvals(String key);

	String hget(String key, String filed);

	Long hdel(String key, String filed);

	/**
	 * 移除set中的一个或者多个元素
	 * 
	 * @param key
	 *            key
	 * @param members
	 *            元素
	 * @return Long
	 */
	Long srem(String key, String... members);
}

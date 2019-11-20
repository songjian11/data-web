package com.cs.mobile.api.service.common.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.mobile.api.service.common.RedisService;
import com.cs.mobile.common.utils.JavaSerializer;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis服务
 * 
 * @author wells.wong
 * @date 2018年11月25日
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
	private static final String LOCK_SUCCESS = "OK";
	private static final Long RELEASE_SUCCESS = 1L;
	/**
	 * 表示不存在key 才设置key
	 */
	private static final String SET_IF_NOT_EXIST = "NX";
	/**
	 * 设置的过期时间为毫秒
	 */
	private static final String SET_WITH_EXPIRE_TIME = "PX";

	@Autowired
	private JedisPool jedisPool;

	@Override
	public Jedis getResource() {
		return jedisPool.getResource();
	}

	@Override
	public void set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.set(key, value);
			log.info("Redis set success - " + key + ", value:" + value);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String get(String key) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.get(key);
			log.info("Redis get success - " + key + ", value:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + result);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public void lpush(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.lpush(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis lpush error: " + e.getMessage() + " - " + key);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	@Override
	public String lpop(String key) {
		Jedis jedis = null;
		String result = null;
		try {
			jedis = getResource();
			result = jedis.lpop(key);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis lpop error: " + e.getMessage() + " - " + key);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public Long llen(String key) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = getResource();
			result = jedis.llen(key);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis llen error: " + e.getMessage() + " - " + key);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public void sadd(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.sadd(key, values);
			log.info("Redis set success - " + key);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis sadd error: " + e.getMessage() + " - " + key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> smembers(String key) {
		Set<String> result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.smembers(key);
			log.info("Redis smembers success - " + key + ", value:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis smembers error: " + e.getMessage() + " - " + key + ", value:" + result);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public Boolean sismembers(String key, String member) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.sismember(key, member);
			log.info("Redis smembers success - " + key + ", value:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis smembers error: " + e.getMessage() + " - " + key + ", value:" + result);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public void smove(String oldKey, String newKey, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.smove(oldKey, newKey, value);
			log.info("Redis smove success - oldKey " + oldKey + " , newKey" + newKey + ", value:" + value);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis smove success - oldKey " + oldKey + " , newKey" + newKey + ", value:" + value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object eval(String script, int keyCnt, String... params) {
		Jedis jedis = null;
		Object object = null;
		try {
			jedis = getResource();
			object = jedis.eval(script, keyCnt, params);
			log.info("Redis eval success - script  {} , params -> {}", script, Arrays.asList(params));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis eval success - script  {} , params -> {}", script, Arrays.asList(params));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return object;
	}

	@Override
	public Long del(String key) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = getResource();
			result = jedis.del(key);
			log.info("Redis del success - key " + key);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis eval success - key " + key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public void setObject(String key, Object value, int expireTime) {
		Jedis jedis = null;
		try {
			byte[] binaryId = key.getBytes();
			jedis = getResource();
			if (jedis != null) {
				jedis.set(binaryId, new JavaSerializer().serialize(value));
				if (expireTime > 0) {
					jedis.expire(binaryId, expireTime);
				}
			}
		} catch (IOException e) {
			log.error("Redis setObject fail " + key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object getObject(String key) {
		Jedis jedis = null;
		Object result = null;
		try {
			jedis = getResource();
			byte[] data = jedis.get(key.getBytes());
			return new JavaSerializer().unserialize(data);
		} catch (Exception e) {
			log.error("Redis getObject fail " + key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
		Jedis jedis = null;
		boolean result;
		try {
			jedis = getResource();
			String string = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
			result = LOCK_SUCCESS.equalsIgnoreCase(string);
		} catch (Exception e) {
			log.error("Redis try get distributedLock fail - key " + lockKey + " requestId - " + requestId);
			result = false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return result;
	}

	@Override
	public boolean releaseDistributedLock(String lockKey, String requestId) {
		Jedis jedis = null;
		boolean result;
		try {
			jedis = getResource();
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			Object object = jedis.eval(script, Collections.singletonList(lockKey),
					Collections.singletonList(requestId));
			result = RELEASE_SUCCESS.equals(object);
		} catch (Exception e) {
			log.error("e -> {}", e);
			result = false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public Long hset(String key, String filed, String value) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = getResource();
			result = jedis.hset(key, filed, value);
			log.info("Redis hset success - " + key);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis hset error: " + e.getMessage() + " - " + key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public List<String> hvals(String key) {
		List<String> result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hvals(key);
			log.info("Redis hset success - " + key);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis hset error: " + e.getMessage() + " - " + key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public String hget(String key, String filed) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hget(key, filed);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis hget error: " + e.getMessage() + " - " + key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public Long hdel(String key, String filed) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.hdel(key, filed);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis hdel error: " + e.getMessage() + " - " + key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public Long srem(String key, String... members) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.srem(key, members);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Redis srem error: " + e.getMessage() + " - " + key + " members - " + Arrays.asList(members));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}
}

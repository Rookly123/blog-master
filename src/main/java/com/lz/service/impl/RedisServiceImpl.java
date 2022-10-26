package com.lz.service.impl;

import com.lz.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 读写Redis相关操作
 * @Author: Naccl
 * @Date: 2020-09-27
 */
@Service
public class RedisServiceImpl implements RedisService {
	@Autowired
	RedisTemplate redisTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Override
	public void saveKVToHash(String hash, Object key, Object value) {
		redisTemplate.opsForHash().put(hash, key, value);
	}

	@Override
	public void saveMapToHash(String hash, Map map) {
		redisTemplate.opsForHash().putAll(hash, map);
	}

	@Override
	public Map getMapByHash(String hash) {
		return redisTemplate.opsForHash().entries(hash);
	}

	@Override
	public List getListByKey(String key, long start, long end) {
		return redisTemplate.opsForList().range(key, start, end);
	}

	@Override
	public Object getValueByHashKey(String hash, Object key) {
		return redisTemplate.opsForHash().get(hash, key);
	}

	@Override
	public void incrementByHashKey(String hash, Object key, int increment) {
		if (increment < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		redisTemplate.opsForHash().increment(hash, key, increment);
	}

	@Override
	public void deleteByHashKey(String hash, Object key) {
		redisTemplate.opsForHash().delete(hash, key);
	}

	@Override
	public <T> List<T> getListByValue(String key) {
		List<T> redisResult = (List<T>) redisTemplate.opsForValue().get(key);
		return redisResult;
	}

	@Override
	public <T> void saveListToValue(String key, List<T> list) {
		redisTemplate.opsForValue().set(key, list);
	}

	@Override
	public <T> Map<String, T> getMapByValue(String key) {
		Map<String, T> redisResult = (Map<String, T>) redisTemplate.opsForValue().get(key);
		return redisResult;
	}

	@Override
	public <T> void saveMapToValue(String key, Map<String, T> map) {
		redisTemplate.opsForValue().set(key, map);
	}

	@Override
	public <T> T getObjectByValue(String key, Class t) {
		Object redisResult = redisTemplate.opsForValue().get(key);
		T object = (T) objectMapper.convertValue(redisResult, t);
		return object;
	}


	@Override
	public void incrementByKey(String key, int increment) {
		if (increment < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		redisTemplate.opsForValue().increment(key, increment);
	}

	@Override
	public void saveObjectToValue(String key, Object object) {
		redisTemplate.opsForValue().set(key, object);
	}

	@Override
	public void saveValueToSet(String key, Object value) {
		redisTemplate.opsForSet().add(key, value);
	}

	@Override
	public int countBySet(String key) {
		return redisTemplate.opsForSet().size(key).intValue();
	}

	@Override
	public void deleteValueBySet(String key, Object value) {
		redisTemplate.opsForSet().remove(key, value);
	}

	@Override
	public boolean hasValueInSet(String key, Object value) {
		return redisTemplate.opsForSet().isMember(key, value);
	}

	@Override
	public void deleteCacheByKey(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	@Override
	public void expire(String key, long time) {
		redisTemplate.expire(key, time, TimeUnit.SECONDS);
	}
}

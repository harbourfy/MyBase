package com.brp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

@Component
public class JedisUtils{

	private  Logger logger = LoggerFactory.getLogger(JedisUtils.class);

	@Resource
	private JedisPool jedisPool;

	/**
	 * 根据正则获取匹配的keys
	 * @param pattern
	 */
	public Set<String> keys(String pattern){
		Set<String> set = Sets.newHashSet();
		Jedis jedis = null;
		try{
			jedis = getResource();
			set = jedis.keys(pattern);
		}catch(Exception e){
			logger.warn("keys {}", pattern, e);
		}finally{
			returnResource(jedis);
		}
		return set;
	}
	
	/**
	 * 把key中的值增加1并返回
	 * @param key
	 * 			键
	 * @return
	 * 			值
	 */
	public  Long incr(String key){
		Long value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				value = jedis.incr(key);
				logger.debug("get {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("get {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public  String get(String key){
		String value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				value = jedis.get(key);
				value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
				logger.debug("get {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("get {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public  Object getObject(String key){
		Object value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				value = toObject(jedis.get(getBytesKey(key)));
				logger.debug("getObject {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("getObject {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public  String set(String key, String value, int cacheSeconds){
		String result = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.set(key, value);
			if(cacheSeconds != 0){
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("set {} = {}", key, value);
		}catch(Exception e){
			logger.warn("set {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public  String setObject(String key, Object value, int cacheSeconds){
		String result = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.set(getBytesKey(key), toBytes(value));
			if(cacheSeconds != 0){
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObject {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setObject {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取List缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public  List<String> getList(String key){
		List<String> value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				value = jedis.lrange(key, 0, -1);
				logger.debug("getList {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("getList {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取List缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	/*public  List<Object> getObjectList(String key){
		List<Object> value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
				value = Lists.newArrayList();
				for(byte[] bs : list){
					value.add(toObject(bs));
				}
				logger.debug("getObjectList {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("getObjectList {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}*/

	/**
	 * 设置List缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 *//*
	public  long setList(String key, List<String> value, int cacheSeconds){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				jedis.del(key);
			}
			String[] vs = new String[value.size()];
			result = jedis.rpush(key, value.toArray(vs));
			if(cacheSeconds != 0){
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setList {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setList {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}*/

	/**
	 * 设置List缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
/*	public  long setObjectList(String key, List<Object> value, int cacheSeconds){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				jedis.del(key);
			}
			List<byte[]> list = Lists.newArrayList();
			for(Object o : value){
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
			if(cacheSeconds != 0){
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObjectList {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setObjectList {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}*/

	/**
	 * 向List缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  long listAdd(String key, String...value){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.rpush(key, value);
			logger.debug("listAdd {} = {}", key, value);
		}catch(Exception e){
			logger.warn("listAdd {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向List缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  long listObjectAdd(String key, Object...value){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			List<byte[]> list = Lists.newArrayList();
			for(Object o : value){
				list.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
			logger.debug("listObjectAdd {} = {}", key, value);
		}catch(Exception e){
			logger.warn("listObjectAdd {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public  Set<String> getSet(String key){
		Set<String> value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				value = jedis.smembers(key);
				logger.debug("getSet {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("getSet {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public  Set<Object> getObjectSet(String key){
		Set<Object> value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				value = Sets.newHashSet();
				Set<byte[]> set = jedis.smembers(getBytesKey(key));
				for(byte[] bs : set){
					value.add(toObject(bs));
				}
				logger.debug("getObjectSet {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("getObjectSet {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public  long setSet(String key, Set<String> value, int cacheSeconds){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				jedis.del(key);
			}
			String[] vs = new String[value.size()];
			result = jedis.sadd(key, value.toArray(vs));
			if(cacheSeconds != 0){
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setSet {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setSet {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public  long setObjectSet(String key, Set<Object> value, int cacheSeconds){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				jedis.del(key);
			}
			Set<byte[]> set = Sets.newHashSet();
			for(Object o : value){
				set.add(toBytes(o));
			}
			result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
			if(cacheSeconds != 0){
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObjectSet {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setObjectSet {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  long setSetAdd(String key, String...value){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.sadd(key, value);
			logger.debug("setSetAdd {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setSetAdd {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}


	/**
	 * 设置List集合
	 * @param key
	 * @param list
	 */
	public void setList(String key ,List<?> list,int cacheSeconds){
		long result = 0;
		Jedis jedis = null;
		try {
            jedis = getResource();
            if(jedis.exists(key)){
                jedis.del(key);
            }
            if(list !=null&&list.size()>0){
                jedis.set(key.getBytes(), SerializeUtil.serializeList(list));
            }else{//如果list为空,则设置一个空
                jedis.set(key.getBytes(), "".getBytes());
            }
            if(cacheSeconds != 0){
                jedis.expire(key, cacheSeconds);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取List集合
	 * @param key
	 * @return
	 */
	public List<?> getObjectList (String key){

        List<String> value = null;
        Jedis jedis = null;
        try{
            jedis = getResource();
            byte[] data = jedis.get(key.getBytes());
            return SerializeUtil.unserializeList(data);
        }catch(Exception e){
            logger.warn("getObjectList {} = {}", key, value, e);
        }finally{
            returnResource(jedis);
        }
        return value;

	}



	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  long setSetObjectAdd(String key, Object...value){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			Set<byte[]> set = Sets.newHashSet();
			for(Object o : value){
				set.add(toBytes(o));
			}
			result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
			logger.debug("setSetObjectAdd {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setSetObjectAdd {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public  Map<String, String> getMap(String key){
		Map<String, String> value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				value = jedis.hgetAll(key);
				logger.debug("getMap {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("getMap {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public  Map<String, Object> getObjectMap(String key){
		Map<String, Object> value = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				value = Maps.newHashMap();
				Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
				for(Map.Entry<byte[], byte[]> e : map.entrySet()){
					value.put(StringUtils.toString(e.getKey()), toObject(e.getValue()));
				}
				logger.debug("getObjectMap {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("getObjectMap {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public  <E> E getOneObjectMap2List(String key, String field){
		List<Object> list = getObjectMap2List(key, field);
		return (list != null && list.size() > 0) ? (E) list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public  <E>List<E> getObjectMap2List(String key, String...fields){
		Jedis jedis = null;
		List<E> value = Lists.newArrayList();
		if(fields==null || fields.length==0){
			Map<String, Object> map = getObjectMap(key);
			if(map != null){
				for(Map.Entry<String, Object> e : map.entrySet()){
					value.add((E)e.getValue());
				}
			}
			return value;
		}
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				final byte[][] params = new byte[fields.length][];
				for(int i = 0; i < fields.length; i++){
					params[i] = getBytesKey(fields[i]);
				}
				List<byte[]> list = jedis.hmget(getBytesKey(key), params);
				for(byte[] bytes : list){
					value.add((E)toObject(bytes));
				}
				logger.debug("getObjectMap {} = {}", key, value);
			}
		}catch(Exception e){
			logger.warn("getObjectMap {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public  String setMap(String key, Map<String, String> value, int cacheSeconds){
		String result = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				jedis.del(key);
			}
			result = jedis.hmset(key, value);
			if(cacheSeconds != 0){
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setMap {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setMap {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public  String setObjectMap(String key, Map<String, ?> value, int cacheSeconds){
		String result = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				jedis.del(key);
			}
			Map<byte[], byte[]> map = Maps.newHashMap();
			for(Map.Entry<String, ?> e : value.entrySet()){
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			if(cacheSeconds != 0){
				jedis.expire(key, cacheSeconds);
			}
			logger.debug("setObjectMap {} = {}", key, value);
		}catch(Exception e){
			logger.warn("setObjectMap {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  String mapPut(String key, Map<String, String> value){
		String result = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.hmset(key, value);
			logger.debug("mapPut {} = {}", key, value);
		}catch(Exception e){
			logger.warn("mapPut {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  String mapObjectPut(String key, Map<String, Object> value){
		String result = null;
		Jedis jedis = null;
		try{
			jedis = getResource();
			Map<byte[], byte[]> map = Maps.newHashMap();
			for(Map.Entry<String, Object> e : value.entrySet()){
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			logger.debug("mapObjectPut {} = {}", key, value);
		}catch(Exception e){
			logger.warn("mapObjectPut {} = {}", key, value, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  long mapRemove(String key, String mapKey){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.hdel(key, mapKey);
			logger.debug("mapRemove {}  {}", key, mapKey);
		}catch(Exception e){
			logger.warn("mapRemove {}  {}", key, mapKey, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  long mapObjectRemove(String key, String mapKey){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
			logger.debug("mapObjectRemove {}  {}", key, mapKey);
		}catch(Exception e){
			logger.warn("mapObjectRemove {}  {}", key, mapKey, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  boolean mapExists(String key, String mapKey){
		boolean result = false;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.hexists(key, mapKey);
			logger.debug("mapExists {}  {}", key, mapKey);
		}catch(Exception e){
			logger.warn("mapExists {}  {}", key, mapKey, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 判断Map缓存中的Key是否存在
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public  boolean mapObjectExists(String key, String mapKey){
		boolean result = false;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
			logger.debug("mapObjectExists {}  {}", key, mapKey);
		}catch(Exception e){
			logger.warn("mapObjectExists {}  {}", key, mapKey, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  long del(String key){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(key)){
				result = jedis.del(key);
				logger.debug("dal {}", key);
			}else{
				logger.debug("dal {} not exists", key);
			}
		}catch(Exception e){
			logger.warn("dal {}", key, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  long delObject(String key){
		long result = 0;
		Jedis jedis = null;
		try{
			jedis = getResource();
			if(jedis.exists(getBytesKey(key))){
				result = jedis.del(getBytesKey(key));
				logger.debug("delObject {}", key);
			}else{
				logger.debug("delObject {} not exists", key);
			}
		}catch(Exception e){
			logger.warn("delObject {}", key, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  boolean exists(String key){
		boolean result = false;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.exists(key);
			logger.debug("exists {}", key);
		}catch(Exception e){
			logger.warn("exists {}", key, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 缓存是否存在
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public  boolean existsObject(String key){
		boolean result = false;
		Jedis jedis = null;
		try{
			jedis = getResource();
			result = jedis.exists(getBytesKey(key));
			logger.debug("existsObject {}", key);
		}catch(Exception e){
			logger.warn("existsObject {}", key, e);
		}finally{
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取资源
	 * 
	 * @return
	 * @throws JedisException
	 */
	public  Jedis getResource() throws JedisException{
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			// logger.debug("getResource.", jedis);
		}catch(JedisException e){
			logger.warn("getResource.", e);
			returnBrokenResource(jedis);
			throw e;
		}
		return jedis;
	}

	/**
	 * 归还资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	public  void returnBrokenResource(Jedis jedis){
		if(jedis != null){
			jedisPool.returnBrokenResource(jedis);
		}
	}

	/**
	 * 释放资源
	 * 
	 * @param jedis
	 * @param isBroken
	 */
	public  void returnResource(Jedis jedis){
		if(jedis != null){
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 获取byte[]类型Key
	 * 
	 * @param key
	 * @return
	 */
	public  byte[] getBytesKey(Object object){
		if(object instanceof String){
			return StringUtils.getBytes((String) object);
		}else{
			return serialize(object);
		}
	}

	/**
	 * Object转换byte[]类型
	 * 
	 * @param key
	 * @return
	 */
	public  byte[] toBytes(Object object){
		return serialize(object);
	}

	/**
	 * byte[]型转换Object
	 * 
	 * @param key
	 * @return
	 */
	public  Object toObject(byte[] bytes){
		return unserialize(bytes);
	}

	
	public  byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			if (object != null){
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				return baos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 反序列化对象
	 * @param bytes
	 * @return
	 */
	public  Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			if (bytes != null && bytes.length > 0){
				bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais);
				return ois.readObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

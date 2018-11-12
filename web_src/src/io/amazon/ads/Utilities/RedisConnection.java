package io.amazon.ads.Utilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * This class wraps a Jedis instance for executing transactions. Created by specifying a Jedis connection
 * pool object in RedisEngine.
 * @see RedisEngine
 * @see JedisPool
 * @see Jedis
 */
public class RedisConnection {
	
	private static final Logger logger = Logger.getLogger(RedisConnection.class);
	private Jedis jedis;
	
	/**
	 * The constructor of this object. It simply request a Jedis connection from the given connection pool.
	 * @param jedisPool The Jedis connection pool through which one requests a connection.
	 * @see JedisPool
	 * @see Jedis
	 */
	public RedisConnection(JedisPool jedisPool) {
		try {
			this.jedis = jedisPool.getResource();
		} catch (JedisException e) {
			logger.error("Error when getting a Jedis connection from the connection pool.", e);
		}
	}
	
	/**
	 * Add a pair of key and value into redis using the given jedis connection object. The key 
	 * and value mush both be String types. Usually used after calling <code>getJedisConn()</code>.
	 * @param key The key used to be stored in redis.
	 * @param value The value to be stored in redis under <code>key</code>.
	 * @see #getValues(String)
	 */
	public void addPair(String key, String value) {
		try {
			jedis.rpush(key, value);
		} catch (JedisException e) {
			logger.error("Redis Transaction error during selecting ads.", e);
		}
	}
	
	/**
	 * Retrieve all values stored under a given key using the jedis connection object.
	 * @param key The key under which the values are to be retrieved.
	 * @return A list of values stored under the key.
	 * @see #addPair(String, String)
	 */
	public List<String> getValues(String key) {
		List<String> values = new ArrayList<>();
		try {
			values = jedis.lrange(key, 0, -1);
		} catch (JedisException e) {
			logger.error("Redis Transaction error during selecting ads.", e);
		}
		return values;
	}
}

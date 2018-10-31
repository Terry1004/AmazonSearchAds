package io.amazon.ads.Utilities;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * This class is able to connect to Redis server, add to and retrieve data from it
 */
public class RedisEngine {
	
	private static RedisEngine instance;
	private String redisHost = "";
	private JedisPool jedisPool;
	
	/**
	 * A protected constructor defined in order to ensure that at most one <code>RedisEngnie</code> 
	 * instance can exist and this instance can only be initialized by the static method <code>
	 * getInstance</code> from outside of this class
	 * @param redisHost The host name of the Redis server. The server name "localhost" and 
	 * the default port 6379 is always used. 
	 * @see #getInstance(String)
	 */
	protected RedisEngine(String redisHost) {
		this.redisHost = redisHost;
		jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
		System.out.println("Jeids Server connection successful");
	}
	
	/**
	 * If no <code>RedisEngine</code> instance is initialized, initialize one using the given parameters
	 * and return it. Otherwise, return the already initialized instance.
	 * @param redisHost The host name of the Redis server. The server name "localhost" and 
	 * the default port 6379 is always used. 
	 * @return RedisEngine The instance of this RedisEngine class.
	 * @see #RedisEngine(String)
	 */
	public static RedisEngine getInstance(String redisHost) {
		if (instance == null) {
			instance = new RedisEngine(redisHost);
		}
		return instance;
	}
	
	/**
	 * Add a pair of key and value into redis. The key and value mush both be String types.
	 * @param key The key used to be stored in redis.
	 * @param value The value to be stored in redis under <code>key</code>
	 * @see #getValues(String)
	 */
	public void addPair(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.rpush(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	/**
	 * Retrieve all values stored under a given key
	 * @param key The key under which the values are to be retrieved
	 * @return A list of values stored under the key
	 * @see #addPair(String, String)
	 */
	public List<String> getValues(String key) {
		Jedis jedis = null;
		try {
			jedis  = jedisPool.getResource();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return jedis.lrange(key, 0, -1);
	}
	
}

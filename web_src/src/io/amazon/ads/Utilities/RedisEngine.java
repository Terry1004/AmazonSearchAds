package io.amazon.ads.Utilities;
import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * This class is able to connect to Redis server, add to and retrieve data from it
 */
public class RedisEngine {
	
	private static final Logger logger = Logger.getLogger(RedisEngine.class);
	private static RedisEngine instance;
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
		try {
			jedisPool = new JedisPool(new JedisPoolConfig(), redisHost);
			jedisPool.getResource().flushAll();
			logger.info("RedisEngine successfully initialized");
		} catch (Exception e) {
			logger.error("RedisEngine fails to be initialized", e);
		}
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
	 * Get a Jedis connection object from the connection pool. It is important to close the Jedis connection
	 * after all transactions are done.
	 * @return A jedis connection object from the connection pool.
	 */
	public Jedis getJedisConn() {
		return jedisPool.getResource();
	}
	
	/**
	 * Add a pair of key and value into redis using the given jedis connection object. The key 
	 * and value mush both be String types. Usually used after calling <code>getJedisConn()</code>.
	 * @param key The key used to be stored in redis.
	 * @param value The value to be stored in redis under <code>key</code>.
	 * @param jedis The Jedis connection object.
	 * @see #getValues(String)
	 * @see #getJedisConn()
	 */
	public static void addPair(String key, String value, Jedis jedis) {
		jedis.rpush(key, value);
	}
	
	/**
	 * Retrieve all values stored under a given key using the jedis connection object.
	 * @param key The key under which the values are to be retrieved.
	 * @return A list of values stored under the key.
	 * @see #addPair(String, String)
	 */
	public static List<String> getValues(String key, Jedis jedis) {
		return jedis.lrange(key, 0, -1);
	}
	
}

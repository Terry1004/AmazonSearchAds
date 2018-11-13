package io.amazon.ads.Utilities;

import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * This class holds a Redis connection pool and can generate RedisConnection object when transactions
 * need to be done.
 * @see RedisConnection
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
			logger.info("RedisEngine successfully initialized.");
		} catch (Exception e) {
			logger.error("RedisEngine fails to be initialized.", e);
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
	 * Get a RedisConnection object from the connection pool. It is important to close the connection
	 * after all transactions are done.
	 * @return RedisConnection object for executing transactions.
	 * @see RedisConnection
	 */
	public RedisConnection getRedisConnection() {
		return new RedisConnection(jedisPool);
	}
	
	
}

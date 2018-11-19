package io.amazon.ads.Database;

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
	private JedisPool jedisInvertedIndexPool;
	private JedisPool jedisSynonymsPool;
	
	/**
	 * A protected constructor defined in order to ensure that at most one <code>RedisEngnie</code> 
	 * instance can exist and this instance can only be initialized by the static method <code>
	 * getInstance</code> from outside of this class
	 * @param redisHost The host name of the Redis server. 
	 * @param redisInvertedIndexPort The port number of the Redis server for storing inverted indices.
	 * @param redisSynonymsPort The port number of the Redis server for storing synonyms of words.
	 * @see #getInstance(String)
	 */
	protected RedisEngine(String redisHost, int redisInvertedIndexPort, int redisSynonymsPort) {
		try {
			jedisInvertedIndexPool = new JedisPool(new JedisPoolConfig(), redisHost, redisInvertedIndexPort);
			jedisInvertedIndexPool.getResource().flushAll(); // only for testing purpose
			jedisSynonymsPool = new JedisPool(new JedisPoolConfig(), redisHost, redisSynonymsPort);
			jedisSynonymsPool.getResource().flushAll(); // only for testing purpose
			logger.info("RedisEngine successfully initialized.");
		} catch (Exception e) {
			logger.error("RedisEngine fails to be initialized.", e);
		}
	}
	
	/**
	 * If no <code>RedisEngine</code> instance is initialized, initialize one using the given parameters
	 * and return it. Otherwise, return the already initialized instance.
	 * @param redisHost The host name of the Redis server. 
	 * @param redisInvertedIndexPort The port number of the Redis server for storing inverted indices.
	 * @param redisSynonymsPort The port number of the Redis server for storing synonyms of words.
	 * @return RedisEngine The instance of this RedisEngine class.
	 * @see #RedisEngine(String)
	 */
	public static RedisEngine getInstance(String redisHost, int redisInvertedIndexPort, int redisSynonymsPort) {
		if (instance == null) {
			instance = new RedisEngine(redisHost, redisInvertedIndexPort, redisSynonymsPort);
		}
		return instance;
	}
	
	/**
	 * Get a RedisConnection object from the connection pool. It is important to close the connection
	 * after all transactions are done.
	 * @return RedisConnection object for executing transactions on redis server storing inverted indices .
	 * @see RedisConnection
	 */
	public RedisConnection getRedisInvertedIndexConnection() {
		return new RedisConnection(jedisInvertedIndexPool);
	}
	
	/**
	 * Get a RedisConnection object from the connection pool. It is important to close the connection
	 * after all transactions are done.
	 * @return RedisConnection object for executing transactions on redis server storing word synonyms.
	 * @see RedisConnection
	 */
	public RedisConnection getRedisSynonymsConnection() {
		return new RedisConnection(jedisSynonymsPool);
	}
	
	
}

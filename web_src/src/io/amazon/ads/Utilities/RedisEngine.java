package io.amazon.ads.Utilities;

/**
 * This class is able to connect to Redis server, add to and retrieve data from it
 */
public class RedisEngine {
	
	private static RedisEngine instance;
	private String redisHost = "";
	
	/**
	 * A protected constructor defined in order to ensure that at most one <code>RedisEngnie</code> 
	 * instance can exist and this instance can only be initialized by the static method <code>
	 * getInstance</code> from outside of this class
	 * @param redisHost The string of the host name of the Redis server. The server name "localhost" and 
	 * the default port 6379 is always used. 
	 * @see #getInstance(String)
	 */
	protected RedisEngine(String redisHost) {
		this.redisHost = redisHost;
	}
	
	/**
	 * If no <code>RedisEngine</code> instance is initialized, initialize one using the given parameters
	 * and return it. Otherwise, return the already initialized instance.
	 * @param redisHost The string of the host name of the Redis server. The server name "localhost" and 
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
	
	public String getRedisHost() {
		return redisHost;
	}
}

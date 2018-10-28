package io.amazon.ads.Utilities;

public class RedisEngine {
	
	private static RedisEngine instance;
	private String redisHost = "";
	
	protected RedisEngine(String redisHost) {
		this.redisHost = redisHost;
	}
	
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

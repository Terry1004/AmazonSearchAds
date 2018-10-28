package io.amazon.ads;

import io.amazon.ads.Utilities.MysqlEngine;
import io.amazon.ads.Utilities.RedisEngine;

public class SearchAdsEngine {
	
	private static SearchAdsEngine instance;
	private RedisEngine redisEngine;
	private MysqlEngine mysqlEngine;
	private String adsDataFilePath = "";
	private String budgetDataFilePath = "";
	
	protected SearchAdsEngine(RedisEngine redisEngine, MysqlEngine mysqlEngine, String adsDataFilePath, String budgetDataFilePath) {
		this.redisEngine = redisEngine;
		this.mysqlEngine = mysqlEngine;
		this.adsDataFilePath = adsDataFilePath;
		this.budgetDataFilePath = budgetDataFilePath;
	}
	
	public static SearchAdsEngine getInstance(RedisEngine redisEngine, MysqlEngine mysqlEngine, String adsDataFilePath, String budgetDataFilePath) {
		if (instance == null) {
			instance = new SearchAdsEngine(redisEngine, mysqlEngine, adsDataFilePath, budgetDataFilePath);
		}
		return instance;
	}
	
	public void init() {
		
	}
	
	public RedisEngine getRedisEngine() {
		return redisEngine;
	}
	
	public MysqlEngine getMysqlEngine() {
		return mysqlEngine;
	}
	
	public String getAdsDataFilePath() {
		return adsDataFilePath;
	}
	
	public String getBudgetDataFilePath() {
		return budgetDataFilePath;
	}

}

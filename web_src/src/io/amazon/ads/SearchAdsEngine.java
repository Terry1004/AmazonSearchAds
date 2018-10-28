package io.amazon.ads;

import java.util.ArrayList;
import java.util.List;

import io.amazon.ads.StaticObjs.Ad;
import io.amazon.ads.Utilities.MysqlEngine;
import io.amazon.ads.Utilities.RedisEngine;

/**
 * This class is able to handle incoming queries by returning a sorted list of ads information by
 * using Redis and MySQL access provided by <code>MySQLEngine</code> and <code>RedisEngine</code> 
 * respectively.
 * @see MySQLEngine
 * @see RedisEngine
 */
public class SearchAdsEngine {
	
	private static SearchAdsEngine instance;
	private RedisEngine redisEngine;
	private MysqlEngine mysqlEngine;
	private String adsDataFilePath = "";
	private String budgetDataFilePath = "";
	
	/**
	 * A protected constructor defined in order to ensure that at most one <code>SearchAdsEngine
	 * </code> instance can exist and this instance can only be initialized by the static method 
	 * <code>getInstance</code> from outside of this class.
	 * @param redisEngine The <code>RedisEngine</code> object that provides Redis server access.
	 * @param mysqlEngine The <code>MysqlEngine</code> object that provides MySQL server access.
	 * @param adsDataFilePath The path to the file that stores ads data.
	 * @param budgetDataFilePath The path to the file that stores budget data.
	 * @see #getInstance(RedisEngine, MysqlEngine, String, String)
	 * @see RedisEngine
	 * @see MysqlEngine
	 */
	protected SearchAdsEngine(RedisEngine redisEngine, MysqlEngine mysqlEngine, String adsDataFilePath, String budgetDataFilePath) {
		this.redisEngine = redisEngine;
		this.mysqlEngine = mysqlEngine;
		this.adsDataFilePath = adsDataFilePath;
		this.budgetDataFilePath = budgetDataFilePath;
	}
	
	/**
	 * If no <code>SearchAdsEngine</code> instance is initialized, initialize one using the given parameters
	 * and return it. Otherwise, return the already initialized instance.
	 * @param redisEngine The <code>RedisEngine</code> object that provides Redis server access.
	 * @param mysqlEngine The <code>MysqlEngine</code> object that provides MySQL server access.
	 * @param adsDataFilePath The path to the file that stores ads data.
	 * @param budgetDataFilePath The path to the file that stores budget data.
	 * @return SearchAdsEngine The instance of this SearchAdsEngine class.
	 * @see RedisEngine
	 * @see MySQLEngine
	 * @see #SearchAdsEngine(RedisEngine, MysqlEngine, String, String)
	 */
	public static SearchAdsEngine getInstance(RedisEngine redisEngine, MysqlEngine mysqlEngine, String adsDataFilePath, String budgetDataFilePath) {
		if (instance == null) {
			instance = new SearchAdsEngine(redisEngine, mysqlEngine, adsDataFilePath, budgetDataFilePath);
		}
		return instance;
	}
	
	/**
	 * Initialize the instance by loading ads and budget data into MySQL amd Redis server
	 * @see #loadAds()
	 * @see #loadBudget()
	 * @return isInit Implies if this <code>SearchAdsEngine</code> is successfully initialized
	 */
	public Boolean init() {
		Boolean isInit = true;
		loadAds();
		loadBudget();
		return isInit;
	}
	
	public List<Ad> selectAds(String query) {
		List<Ad> ads = new ArrayList<>();
		ads.add(new Ad("test"));
		ads.add(new Ad(query));
		return ads;
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
	
	/**
	 * Load Ads data into MySQL and Redis server
	 */
	private void loadAds() {
		
	}
	
	/**
	 * Load budget data into MySQL server
	 */
	private void loadBudget() {
		
	}

}

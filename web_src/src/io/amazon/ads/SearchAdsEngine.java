package io.amazon.ads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

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
	 * <code>getInstance</code> from outside of this class. When creating a new instance, ads
	 * and budget information is also loaded into Redis and MySQL.
	 * @param redisEngine The <code>RedisEngine</code> object that provides Redis server access.
	 * @param mysqlEngine The <code>MysqlEngine</code> object that provides MySQL server access.
	 * @param adsDataFilePath The path to the file that stores ads data.
	 * @param budgetDataFilePath The path to the file that stores budget data.
	 * @see #getInstance(RedisEngine, MysqlEngine, String, String)
	 * @see #loadAds()
	 * @see #loadBudget()
	 * @see RedisEngine
	 * @see MysqlEngine
	 */
	protected SearchAdsEngine(RedisEngine redisEngine, MysqlEngine mysqlEngine, String adsDataFilePath, String budgetDataFilePath) {
		try {
			this.redisEngine = redisEngine;
			this.mysqlEngine = mysqlEngine;
			this.adsDataFilePath = adsDataFilePath;
			this.budgetDataFilePath = budgetDataFilePath;
			loadAds();
			loadBudget();
			System.out.println("searchAdsEngine successfully initialized");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("searchAdsEngine fails to be initialized");
		}
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
	
	public List<Ad> selectAds(String query) {
		List<Ad> ads = new ArrayList<>();
		redisEngine.addPair("test", "test");
		redisEngine.addPair("test", query);
		for (String title: redisEngine.getValues("test")) {
			Ad ad = new Ad();
			ad.title = title;
			ads.add(ad);
		}
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
	 * Parse a line of a json string into an Ad object. If adId or campaignId is not found, null
	 * is returned instead. Price is default to 100.
	 * @param line A json string of an Ad object.
	 * @param counter The line number (starting from 0), used for logging.
	 * @return An Ad object parsed from the json string. Null is returned if adId or campaignId
	 * is not found. Price is 100 if price is not found in the json string.
	 */
	private Ad parseAd(String line, int counter) {
		JSONObject adJson = new JSONObject(line);
		Ad ad = new Ad();
		if (adJson.isNull("adId")) {
			System.out.println("adId not found: line " + counter);
			return null;
		}
		if (adJson.isNull("campaignId")) {
			System.out.println("campaignId not found: line " + counter);
			return null;
		}
		ad.brand = adJson.getString("brand");
		ad.thumbnail = adJson.getString("thumbnail");
		ad.title = adJson.getString("title");
		ad.detail_url = adJson.getString("detail_url");
		ad.category =  adJson.getString("category");
		ad.adId = adJson.getLong("adId");
		ad.campaignId = adJson.getLong("campaignId");
		ad.price = adJson.isNull("price") ? 100.0 : adJson.getDouble("price");
		ad.keyWords = new ArrayList<String>();
		return ad;
	}
	
	/**
	 * Load ads data into MySQL and Redis. Ads without adId or campaignId will be ignored
	 * @see #parseAd(String, int)
	 */
	private void loadAds() {
		try (BufferedReader brAd = new BufferedReader(new FileReader(adsDataFilePath))) {
			String line;
			int counter = 0;
			while ((line = brAd.readLine()) != null) {
				Ad ad = parseAd(line, counter);
				if (ad != null) {
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load budget data into MySQL server
	 */
	private void loadBudget() {
		
	}

}

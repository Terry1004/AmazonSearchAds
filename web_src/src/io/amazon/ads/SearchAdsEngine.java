package io.amazon.ads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.amazon.ads.StaticObjs.Ad;
import io.amazon.ads.Utilities.MysqlConnection;
import io.amazon.ads.Utilities.MysqlEngine;
import io.amazon.ads.Utilities.RedisConnection;
import io.amazon.ads.Utilities.RedisEngine;
import io.amazon.ads.Utilities.Utils;

/**
 * This class is able to handle incoming queries by returning a sorted list of ads information by
 * using Redis and MySQL access provided by <code>MySQLEngine</code> and <code>RedisEngine</code> 
 * respectively.
 * @see MySQLEngine
 * @see RedisEngine
 */
public class SearchAdsEngine {
	
	private static final Logger logger = Logger.getLogger(SearchAdsEngine.class);
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
//			loadBudget();
			logger.info("SearchAdsEngine successfully initialized.");
		} catch (Exception e) {
			logger.error("SearchAdsEngine fails to be initialized.", e);
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
		RedisConnection redisConnection = null;
		redisConnection = redisEngine.getRedisConn();
		redisConnection.addPair("test", "test");
		redisConnection.addPair("test", query);
		for (String title: redisConnection.getValues("test")) {
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
		if (adJson.isNull("ad_id")) {
			logger.debug("adId not found at line " + counter);
			return null;
		} else {
			JSONArray array = adJson.getJSONArray("ad_id");
			if (array.isNull(0)) {
				logger.debug("adId not found at line " + counter);
				return null;
			} else {
				ad.adId = array.getLong(0);
			}
		}
		if (adJson.isNull("campaign_id")) {
			logger.debug("campaignId not found at line " + counter);
			return null;
		} else {
			JSONArray array = adJson.getJSONArray("campaign_id");
			if (array.isNull(0)) {
				logger.debug("campaignId not found at line " + counter);
				return null;
			} else {
				ad.campaignId = array.getLong(0);
			}
		}
		if (adJson.isNull("title")) {
			logger.debug("title not found at line " + counter);
			return null;
		} else {
			JSONArray array = adJson.getJSONArray("title");
			if (array.isNull(0)) {
				logger.debug("title not found at line " + counter);
				return null;
			} else {
				ad.title = array.getString(0);
			}
		}
		ad.brand = adJson.optJSONArray("brand").optString(0);
		ad.thumbnail = adJson.optJSONArray("thumbnail").optString(0);
		ad.detail_url = adJson.optJSONArray("detail_url").optString(0);
		ad.category =  adJson.optJSONArray("category").optString(0);
		ad.price = adJson.optJSONArray("price").optDouble(0);
		ad.bidPrice = adJson.optJSONArray("bid_price").optDouble(0);
		ad.keyWords = Utils.splitKeyWords(ad.title);
		return ad;
	}
	
	/**
	 * Load ads data into MySQL and Redis. Ads without adId or campaignId will be ignored.
	 * @see #parseAd(String, int)
	 */
	private void loadAds() {
		MysqlConnection mysqlConnection = mysqlEngine.getMysqlConnection(); // to be used later
		try (BufferedReader brAd = new BufferedReader(new FileReader(adsDataFilePath))) {
			String line;
			int counter = 0;
			while ((line = brAd.readLine()) != null) {
				if (!line.equals("[") && !line.equals("]")) {
					Ad ad = parseAd(line, counter);
					if (ad != null) {
						
					}
				}
				counter += 1;
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

package io.amazon.ads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.amazon.ads.StaticObjs.Ad;
import io.amazon.ads.StaticObjs.Campaign;
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
	private static final Double DEFAULT_PRICE = 100.0;
	private static final Double DEFAULT_BID_PRICE = 100.0;
	private static SearchAdsEngine instance;
	private RedisEngine redisEngine;
	private MysqlEngine mysqlEngine;
	private String adsDataPath = "";
	private String campaignDataPath = "";
	
	/**
	 * A protected constructor defined in order to ensure that at most one <code>SearchAdsEngine
	 * </code> instance can exist and this instance can only be initialized by the static method 
	 * <code>getInstance</code> from outside of this class. When creating a new instance, ads
	 * and campaign information is also loaded into Redis and MySQL.
	 * @param redisEngine The <code>RedisEngine</code> object that provides Redis server access.
	 * @param mysqlEngine The <code>MysqlEngine</code> object that provides MySQL server access.
	 * @param adsDataPath The path to the file that stores ads data.
	 * @param campaignDataPath The path to the file that stores campaign data.
	 * @see #getInstance(RedisEngine, MysqlEngine, String, String)
	 * @see #loadAds()
	 * @see #loadcampaign()
	 * @see RedisEngine
	 * @see MysqlEngine
	 */
	protected SearchAdsEngine(RedisEngine redisEngine, MysqlEngine mysqlEngine, String adsDataPath, String campaignDataPath) {
		try {
			this.redisEngine = redisEngine;
			this.mysqlEngine = mysqlEngine;
			this.adsDataPath = adsDataPath;
			this.campaignDataPath = campaignDataPath;
			loadAds();
			loadcampaign();
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
	 * @param campaignDataFilePath The path to the file that stores campaign data.
	 * @return SearchAdsEngine The instance of this SearchAdsEngine class.
	 * @see RedisEngine
	 * @see MySQLEngine
	 * @see #SearchAdsEngine(RedisEngine, MysqlEngine, String, String)
	 */
	public static SearchAdsEngine getInstance(RedisEngine redisEngine, MysqlEngine mysqlEngine, String adsDataPath, String campaignDataPath) {
		if (instance == null) {
			instance = new SearchAdsEngine(redisEngine, mysqlEngine, adsDataPath, campaignDataPath);
		}
		return instance;
	}
	
	public List<Ad> selectAds(String query) {
		List<Ad> ads = new ArrayList<>();
		RedisConnection redisConnection = redisEngine.getRedisConnection();
		MysqlConnection mysqlConnection = mysqlEngine.getMysqlConnection();
		if (redisConnection == null) {
			logger.error("Error when attempting to connect to Redis when selecting ads.");
			return ads;
		}
		if (mysqlConnection == null) {
			logger.error("Error when attempting to connect to SQL database when selecting ads.");
			return ads;
		}
		try {
			Set<Long> selected = new HashSet<>(); // in order to remove duplicate ads
			List<String> keyWords = Utils.splitKeyWords(query);
			for (String keyWord : keyWords) {
				for (String stringAdId: redisConnection.getValues(keyWord)) {
					Long adId = Long.parseLong(stringAdId);
					if (!selected.contains(adId)) {
						ads.add(mysqlConnection.getAd(adId));
						selected.add(adId);
					}
				}
			}
		} finally {
			mysqlConnection.close();
		}
		return ads;
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
			logger.debug("adId not found at line " + counter + ".");
			return null;
		} else {
			JSONArray array = adJson.getJSONArray("ad_id");
			if (array.isNull(0)) {
				logger.debug("adId not found at line " + counter + ".");
				return null;
			} else {
				ad.adId = array.getLong(0);
			}
		}
		if (adJson.isNull("campaign_id")) {
			logger.debug("campaignId not found at line " + counter + ".");
			return null;
		} else {
			JSONArray array = adJson.getJSONArray("campaign_id");
			if (array.isNull(0)) {
				logger.debug("campaignId not found at line " + counter + ".");
				return null;
			} else {
				ad.campaignId = array.getLong(0);
			}
		}
		if (adJson.isNull("title")) {
			logger.debug("title not found at line " + counter + ".");
			return null;
		} else {
			JSONArray array = adJson.getJSONArray("title");
			if (array.isNull(0)) {
				logger.debug("title not found at line " + counter + ".");
				return null;
			} else {
				ad.title = array.getString(0);
			}
		}
		ad.brand = adJson.optJSONArray("brand").optString(0);
		ad.thumbnail = adJson.optJSONArray("thumbnail").optString(0);
		ad.detailUrl = adJson.optJSONArray("detail_url").optString(0);
		ad.category =  adJson.optJSONArray("category").optString(0);
		ad.price = adJson.optJSONArray("price").optDouble(0, DEFAULT_PRICE);
		ad.bidPrice = adJson.optJSONArray("bid_price").optDouble(0, DEFAULT_BID_PRICE);
		ad.keyWords = Utils.splitKeyWords(ad.title);
		return ad;
	}
	
	private Campaign parseCampaign(String line, int counter) {
		JSONObject campaignJson = new JSONObject(line);
		Campaign campaign = new Campaign();
		if (campaignJson.isNull("campaign_id")) {
			logger.debug("campaignId not found at line: " + counter + ".");
			return null;
		} else {
			campaign.campaignId = campaignJson.getLong("campaign_id");
		}
		if (campaignJson.isNull("budget")) {
			logger.debug("budget not found at line; " + counter + ".");
			return null;
		} else {
			campaign.budget = campaignJson.getDouble("budget");
		}
		return campaign;
	}
	
	/**
	 * Load ads data into MySQL and Redis. Ads without adId or campaignId will be ignored. The file
	 * is stored in the format that each line is a json except the first and the last which are '['
	 * and ']' symbols respectively.
	 * @see #parseAd(String, int)
	 */
	private void loadAds() {
		RedisConnection redisConnection = redisEngine.getRedisConnection();
		MysqlConnection mysqlConnection = mysqlEngine.getMysqlConnection();
		if (redisConnection == null) {
			logger.error("Error when connecting to connect to Redis when loading ads.");
			return;
		}
		if (mysqlConnection == null) {
			logger.error("Error when connecting to SQL database when loading ads.");
			return;
		}
		try (BufferedReader brAd = new BufferedReader(new FileReader(adsDataPath))) {
			String line;
			int counter = 0;
			while ((line = brAd.readLine()) != null) {
				if (!line.equals("[") && !line.equals("]")) {
					Ad ad = parseAd(line, counter);
					if (ad != null) {
						mysqlConnection.addAd(ad);
						for (String word : ad.keyWords) {
							redisConnection.addPair(word, Long.toString(ad.adId));
						}
					}
				}
				counter += 1;
			}
		} catch (IOException e) {
			logger.error("Encounter IO error when loading ads.", e);
		} finally {
			mysqlConnection.close();
		}
	}
	
	/**
	 * Load campaign data into MySQL server
	 */
	private void loadcampaign() {
		MysqlConnection mysqlConnection = mysqlEngine.getMysqlConnection();
		if (mysqlConnection == null) {
			logger.error("Error when attempting to connect to SQL database when loadig campaigns.");
			return;
		}
		try (BufferedReader brAd = new BufferedReader(new FileReader(campaignDataPath))) {
			String line;
			int counter = 0;
			while ((line = brAd.readLine()) != null) {
				if (!line.equals("[") && !line.equals("]")) {
					Campaign campaign = parseCampaign(line, counter);
					if (campaign != null) {
						mysqlConnection.addCampaign(campaign);
					}
				}
				counter += 1;
			} 
		} catch(IOException e) {
			logger.error("Encounter IO error when loading campaign data from file.", e);
		} finally {
			mysqlConnection.close();
		}
	}

}

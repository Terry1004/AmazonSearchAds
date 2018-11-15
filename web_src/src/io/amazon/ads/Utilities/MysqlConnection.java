package io.amazon.ads.Utilities;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import io.amazon.ads.StaticObjs.Ad;
import io.amazon.ads.StaticObjs.Campaign;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


/**
 * This class wraps a sql Connection instance for executing transactions. Created by specifying a DataSource
 * instance in RedisEngine.
 * @see MysqlEngine
 * @see DataSource
 * @see Connection
 */
public class MysqlConnection {
	
	private static final Logger logger = Logger.getLogger(MysqlConnection.class);
	private Connection mysqlConnection;
	private String adsTableName;
	private String campaignTableName;
	
	/**
	 * The constructor of this object. It simply request a Mysql connection from the given data source.
	 * @param mysqlDataSource The MySQL data source through which one requests a connection.
	 * @param adsTableName The name of the table storing ads data.
	 * @param campaignTableName The name of the table storing campaign data.
	 * @see DataSource
	 * @see Connection
	 */
	public MysqlConnection(DataSource mysqlDataSource, String adsTableName, String campaignTableName) {
		try {
			mysqlConnection = mysqlDataSource.getConnection();
		} catch (SQLException e) {
			logger.error("Error when requesting SQL connection.", e);
		}
		this.adsTableName = adsTableName;
		this.campaignTableName = campaignTableName;
	}
	
	/**
	 * Close the current connection. Should be enforced after each transaction. Attempting to use a
	 * <code>MysqlConnection</code> instance on which this method <code>closed</code> has been called 
	 * will cause an error.
	 */
	public void close() {
		try {
			mysqlConnection.close();
		} catch (SQLException e) {
			logger.error("Error when closing MySQL connection.", e);
		}
	}
	
	public void addAd(Ad ad) {
		String sqlString = "INSERT INTO " + adsTableName + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement addAd = null;
		try {
			addAd = mysqlConnection.prepareStatement(sqlString);
			addAd.setLong(1, ad.adId);
		  	addAd.setLong(2, ad.campaignId);	  
		  	addAd.setString(3, String.join(", ", ad.keyWords));
		  	addAd.setDouble(4, ad.bidPrice);
		  	addAd.setDouble(5, ad.price);
		  	addAd.setString(6, ad.thumbnail);
		  	addAd.setString(7, ad.brand);
		  	addAd.setString(8, ad.detailUrl);
		  	addAd.setString(9, ad.category);
		  	addAd.setString(10, ad.title);
		  	addAd.executeUpdate();
		} catch (SQLException e) {
			logger.error("SQL error when inserting ad into database with adId: " + ad.adId + ".", e);
		} catch (Exception e) {
			logger.error("Non-SQL error when inserting ad into database with adId " + ad.adId + ".", e);
		} finally {
			try {
				addAd.close();
			} catch (SQLException e) {
				logger.error("SQL error when closing SQL statement for inserting ads.", e);
			} catch (Exception e) {
				logger.error("Non-SQL error when closing SQL statement for inserting ads.", e);
			}
		}
	}
	
	public void addCampaign(Campaign campaign) {
		String sqlString = "INSERT INTO " + campaignTableName + " VALUES(?, ?)";
		PreparedStatement addCampaign = null;
		try {
			addCampaign = mysqlConnection.prepareStatement(sqlString);
			addCampaign.setLong(1, campaign.campaignId);
			addCampaign.setDouble(2, campaign.budget);
			addCampaign.executeUpdate();
		} catch (SQLException e) {
			logger.error("SQL error when inserting campaign into database with campaignId: " + campaign.campaignId + ".", e);
		} catch (Exception e) {
			logger.error("Non-SQL error when inserting campaign into database with campaignId: " + campaign.campaignId + ".", e);
		} finally {
			try {
				addCampaign.close();
			} catch (SQLException e) {
				logger.error("SQL error when closing SQL statement for inserting campaign.", e);
			} catch (Exception e) {
				logger.error("Non-SQL error when closing SQL statement for inserting campaign", e);
			}
		}
	}
	
	public Ad getAd(Long adId) {
		// to complete
		String sqlString = "SELECT * FROM " + adsTableName + " WHERE adId = " + adId;
		PreparedStatement selectAd = null;
		ResultSet resultSet = null;
   	 	Ad ad = new Ad();
   	 	try {
   	 		selectAd = mysqlConnection.prepareStatement(sqlString);
   	 		resultSet = selectAd.executeQuery();
   	 		if (resultSet.next()) {
   	 			ad.adId = resultSet.getLong("adId");
		       	ad.campaignId = resultSet.getLong("campaignId");
		       	ad.keyWords = Arrays.asList(resultSet.getString("keyWords").split(","));
		       	ad.bidPrice = resultSet.getDouble("bid");
		       	ad.price = resultSet.getDouble("price");
		       	ad.thumbnail = resultSet.getString("thumbnail");
		       	ad.brand = resultSet.getString("brand");
		       	ad.detailUrl = resultSet.getString("detailUrl");
		       	ad.category = resultSet.getString("category");
		       	ad.title = resultSet.getString("title");
   	 		} else {
   	 			logger.error("No record found with adId: " + adId + ".");
   	 		}
   	 	} catch (SQLException e) {
   	 		logger.error("SQL error when retrieving ads.", e);
   	 	} catch (Exception e) {
   	 		logger.error("Non-SQL error when retrieving ads.", e);
   	 	} finally {
   	 		try {
   	 			selectAd.close();
   	 		} catch (SQLException e) {
   	 			logger.error("SQL error when closing SQL statement for retrieving ads.", e);
   	 		} catch(Exception e) {
   	 			logger.error("Non-SQL error when closing SQL statement for retrieving ads.", e);
   	 		}
   	 		try {
   	 			resultSet.close();
   	 		} catch (SQLException e) {
   	 			logger.error("SQL error when closing SQL result set of retrieved ads.", e);
   	 		} catch (Exception e) {
   	 			logger.error("Non-SQL error when closing SQL result set of retrieved ads.", e);
   	 		}
   	 	}
		return ad;
	}
}

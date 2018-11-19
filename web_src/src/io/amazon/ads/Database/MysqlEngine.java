package io.amazon.ads.Database;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * This class holds a DataSource instance and can generate MysqlConnection object when transactions
 * need to be done.
 */
public class MysqlEngine {
	
	private static final Logger logger = Logger.getLogger(MysqlEngine.class);
	private static MysqlEngine instance;
	private DataSource mysqlDataSource;
	private String adsTableName = "";
	private String campaignTableName = "";
	
	/**
	 * A protected constructor defined in order to ensure that at most one <code>MysqlEngine</code> 
	 * instance can exist and this instance can only be initialized by the static method <code>
	 * getInstance</code> from outside of this class.
	 * @param dbSourceUrl The url string for java <code>InitialContext</code> instance to lookup for
	 * obtaining a <code>DataSource</code> instance.
	 * @param adsTableName The name of the table storing ads data.
	 * @param campaignTableName The name of the table storing campaign data.
	 * @see #getInstance(String, String, String, String)
	 * @see javax.naming.InitialContext
	 * @see javax.sql.DataSource
	 */
	protected MysqlEngine (String dbSourceUrl, String adsTableName, String campaignTableName) {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			mysqlDataSource = (DataSource) ctx.lookup(dbSourceUrl);
			logger.info("MysqlEngine successfully initialized.");
		} catch (Exception e) {
			logger.error("MysqlEngine fails to be initialized.", e);
		}
		this.adsTableName = adsTableName;
		this.campaignTableName = campaignTableName;
	}
	
	/**
	 * If no <code>MysqlEngine</code> instance is initialized, initialize one using the given parameters
	 * and return it. Otherwise, return the already initialized instance.
	 * @param dbSourceUrl The url string for java <code>InitialContext</code> instance to lookup for
	 * obtaining a <code>DataSource</code> instance.
	 * @param adsTableName The name of the table storing ads data.
	 * @param campaignTableName The name of the table storing campaign data.
	 * @return MysqlEngine The instance of this MysqlEngine class.
	 * @see #MysqlEngine(String, String, String, String)
	 */
	public static MysqlEngine getInstance(String dbSourceUrl, String adsTableName, String campaignTableName) {
		if (instance == null) {
			instance= new MysqlEngine(dbSourceUrl, adsTableName, campaignTableName);
		}
		return instance;
	}
	
	/**
	 * Get a MySQL connection from the connection pool and return the MysqlConnection instance which serves as
	 * a wrapper of a Connection object.
	 * @return A MysqlConnection object that functions as a wrapper of a Connection object for doing transactions.
	 * @see MysqlConnection
	 */
	public MysqlConnection getMysqlConnection() {
		return new MysqlConnection(mysqlDataSource, adsTableName, campaignTableName);
	}
}

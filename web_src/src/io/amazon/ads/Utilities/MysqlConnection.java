package io.amazon.ads.Utilities;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;


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
			logger.error("Error when requesting SQL connection", e);
		}
		this.adsTableName = adsTableName;
		this.campaignTableName = campaignTableName;
	}

}

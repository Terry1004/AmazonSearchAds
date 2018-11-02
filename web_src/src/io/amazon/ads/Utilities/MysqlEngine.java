package io.amazon.ads.Utilities;

import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * This class is able to connect to MySQL server, add to and retrieve data from it.
 */
public class MysqlEngine {
	
	private static MysqlEngine instance;
	private DataSource mysqlData;
	
	/**
	 * A protected constructor defined in order to ensure that at most one <code>MysqlEngine</code> 
	 * instance can exist and this instance can only be initialized by the static method <code>
	 * getInstance</code> from outside of this class.
	 * @param dbSourceUrl The url string for java <code>InitialContext</code> instance to lookup for
	 * obtaining a <code>DataSource</code> instance.
	 * @see #getInstance(String, String, String, String)
	 * @see javax.naming.InitialContext
	 * @see javax.sql.DataSource
	 */
	protected MysqlEngine (String dbSourceUrl) {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			mysqlData = (DataSource) ctx.lookup(dbSourceUrl);
			System.out.println("MysqlEngine successfully initialized");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("MysqlEngine fails to be initialized");
		}
	}
	
	/**
	 * If no <code>MysqlEngine</code> instance is initialized, initialize one using the given parameters
	 * and return it. Otherwise, return the already initialized instance.
	 * @param dbSourceUrl The url string for java <code>InitialContext</code> instance to lookup for
	 * obtaining a <code>DataSource</code> instance.
	 * @return MysqlEngine The instance of this MysqlEngine class.
	 * @see #MysqlEngine(String, String, String, String)
	 */
	public static MysqlEngine getInstance(String dbSourceUrl) {
		if (instance == null) {
			instance= new MysqlEngine(dbSourceUrl);
		}
		return instance;
	}
	
}

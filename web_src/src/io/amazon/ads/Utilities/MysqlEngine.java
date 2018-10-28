package io.amazon.ads.Utilities;

/**
 * This class is able to connect to MySQL server, add to and retrieve data from it.
 */
public class MysqlEngine {
	
	private static MysqlEngine instance;
	private String mysqlHost = "";
	private String mysqlDB = "";
	private String mysqlUser = "";
	private String mysqlPassword = "";
	
	/**
	 * A dummy class defined in order to ensure that at most one <code>MysqlEngine</code> instance 
	 * can exist and this instance can only be initialized by the static method <code>
	 * getInstance</code> from outside of this class.
	 * @param mysqlHost The host name of the server in the form of "ip address: port".
	 * @param mysqlDB The name of the database to be connected to.
	 * @param mysqlUser The username of the MySQL server to be connected to.
	 * @param mysqlPassword The password of the MySQL server to be connected to.
	 * @see #getInstance(String, String, String, String)
	 */
	protected MysqlEngine(String mysqlHost, String mysqlDB, String mysqlUser, String mysqlPassword) {
		this.mysqlHost = mysqlHost;
		this.mysqlDB = mysqlDB;
		this.mysqlUser = mysqlUser;
		this.mysqlPassword = mysqlPassword;
	}
	
	/**
	 * If no <code>MysqlEngine</code> instance is initialized, initialize one using the given parameters
	 * and return it. Otherwise, return the already initialized instance.
	 * @param mysqlHost The string of the host name of the server in the form of "ip address: port".
	 * @param mysqlDB The name of the database to be connected to.
	 * @param mysqlUser The username of the MySQL server to be connected to.
	 * @param mysqlPassword The password of the MySQL server to be connected to.
	 * @return {@link #MysqlEngine(String, String, String, String)} The instance of the (dummy class of the) 
	 * class for connecting to MySQL server, adding to and retrieving data from it.
	 * @see #MysqlEngine(String, String, String, String)
	 */
	public static MysqlEngine getInstance(String mysqlHost, String mysqlDB, String mysqlUser, String mysqlPassword) {
		if (instance == null) {
			instance= new MysqlEngine(mysqlHost, mysqlDB, mysqlUser, mysqlPassword);
		}
		return instance;
	}
	
	public String getMysqlHost() {
		return mysqlHost;
	}
	
	public String getMysqlDB() {
		return mysqlDB;
	}
	
	public String getMysqlUser() {
		return mysqlUser;
	}
	
	public String getMysqlPassword() {
		return mysqlPassword;
	}
}

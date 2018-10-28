package io.amazon.ads.Utilities;

public class MysqlEngine {
	
	private static MysqlEngine instance;
	private String mysqlHost = "";
	private String mysqlDB = "";
	private String mysqlUser = "";
	private String mysqlPassword = "";
	
	protected MysqlEngine(String mysqlHost, String mysqlDB, String mysqlUser, String mysqlPassword) {
		this.mysqlHost = mysqlHost;
		this.mysqlDB = mysqlDB;
		this.mysqlUser = mysqlUser;
		this.mysqlPassword = mysqlPassword;
	}
	
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

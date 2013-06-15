package org.young.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.young.util.PropertyReader;

public class ProxoolPool {

	// private static Logger logger = Logger.getLogger(ProxoolPool.class);
	private static ProxoolDataSource datasource;

	static {
		PropertyReader reader = new PropertyReader();
		datasource = new ProxoolDataSource();
		datasource.setDriver(reader.getString("db.driver"));
		datasource.setDriverUrl(reader.getString("db.url"));
		datasource.setUser(reader.getString("db.username"));
		datasource.setPassword(reader.getString("db.password"));
		datasource.setAlias("datasource");
		datasource.setMaximumConnectionCount(reader.getInt("pool.maxsize"));
		datasource.setMinimumConnectionCount(reader.getInt("pool.minsize"));
		datasource.setMaximumActiveTime(reader.getInt("pool.activetime"));
		datasource.setMaximumConnectionLifetime(reader.getInt("pool.lifetime"));
		datasource.setHouseKeepingSleepTime(reader.getInt("pool.sleeptime"));
		datasource.setHouseKeepingTestSql(reader.getString("pool.testsql"));
		datasource.setTestAfterUse(true);
		datasource.setTestBeforeUse(true);
	}

	public static ProxoolPool pool;

	private ProxoolPool() {
	}

	public static ProxoolPool getInstance() {
		if (pool == null) {
			pool = new ProxoolPool();
		}
		return pool;
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	public void releaseConnection(Connection connection) throws SQLException {
		connection.close();
	}
}

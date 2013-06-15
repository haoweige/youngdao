package org.young.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.young.util.PropertyReader;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Pool {

	private static Logger logger = Logger.getLogger(C3P0Pool.class);

	private static ComboPooledDataSource datasource;

	static {
		PropertyReader reader = new PropertyReader();
		datasource = new ComboPooledDataSource();
		try {
			datasource.setDriverClass(reader.getString("db.driver"));
			datasource.setJdbcUrl(reader.getString("db.url"));
			datasource.setUser(reader.getString("db.username"));
			datasource.setPassword(reader.getString("db.password"));
			datasource.setInitialPoolSize(reader.getInt("pool.initsize"));
			datasource.setMinPoolSize(reader.getInt("pool.minsize"));
			datasource.setMaxPoolSize(reader.getInt("pool.maxsize"));
			datasource.setAcquireIncrement(reader.getInt("pool.increment"));
		} catch (PropertyVetoException e) {
			logger.error(e);
		}
	}

	public static C3P0Pool pool;

	private C3P0Pool() {
	}

	public static C3P0Pool getInstance() {
		if (pool == null) {
			pool = new C3P0Pool();
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

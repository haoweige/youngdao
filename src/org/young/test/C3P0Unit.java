package org.young.test;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import org.junit.Test;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Unit {

	@Test
	public void test() throws PropertyVetoException, SQLException {
		ComboPooledDataSource datasource = new ComboPooledDataSource();
		datasource.setDriverClass("com.mysql.jdbc.Driver");
		datasource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test");
		datasource.setUser("root");
		datasource.setPassword("passw0rd");
		datasource.setInitialPoolSize(20);
		datasource.setMinPoolSize(20);
		datasource.setMaxPoolSize(100);
		datasource.setAcquireIncrement(5);
		System.out.println(datasource.getConnection());
	}
}

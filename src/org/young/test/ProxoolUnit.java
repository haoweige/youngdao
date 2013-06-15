package org.young.test;

import java.sql.SQLException;

import org.junit.Test;
import org.logicalcobwebs.proxool.ProxoolDataSource;

public class ProxoolUnit {

	@Test
	public void test() throws SQLException {
		ProxoolDataSource ds = new ProxoolDataSource();
		ds.setDriver("com.mysql.jdbc.Driver");
		ds.setDriverUrl("jdbc:mysql://127.0.0.1:3306/test");
		ds.setUser("root");
		ds.setPassword("passw0rd");
		ds.setAlias("ds");
		ds.setMaximumConnectionCount(100);
		ds.setMinimumConnectionCount(10);
		ds.setMaximumActiveTime(600 * 1000);
		ds.setMaximumConnectionLifetime(5 * 60 * 60 * 1000);
		ds.setHouseKeepingSleepTime(60 * 60 * 1000);
		ds.setHouseKeepingTestSql("select count(*) from dual");
		ds.setTestAfterUse(true);
		ds.setTestBeforeUse(true);
		System.out.println(ds.getConnection());
	}
}

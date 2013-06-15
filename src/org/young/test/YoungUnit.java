package org.young.test;

import java.util.Vector;

import org.junit.Test;
import org.young.jdbc.DBHelper;

import youngPackage.model.YoungContent;
import youngPackage.model.YoungProperty;

@SuppressWarnings( { "unchecked", "unused"})
public class YoungUnit {

	static String SQL = "FROM BMTJ";

	public static void printEntities(Vector vector, String[] columns) {
		for (int i = 0; i < vector.size(); i++) {
			YoungContent entity = (YoungContent) vector.get(i);
			System.out.println("CONTENTID->" + entity.getContentId());
			for (int j = 0; j < columns.length; j++) {
				String name = columns[j];
				YoungProperty property = entity.getProperty(name);
				if (property != null)
					System.out.println(name + "->" + property.getValue());
			}
		}
	}

	/*
	// @Test
	public void youngSearch() {
		Vector vector = null;
		YoungDB db = new YoungDB();
		long start = System.currentTimeMillis();
		vector = db.search(SQL);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	// @Test
	public void youngQuery() {
		Vector vector = null;
		YoungDB db = new YoungDB();
		long start = System.currentTimeMillis();
		vector = db.query(SQL);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}*/

	// @Test
	public void jdbcQuery() {
		Vector vector = null;
		DBHelper helper = new DBHelper();
		long start = System.currentTimeMillis();
		vector = helper.query(SQL);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	// @Test
	public void queryOrder() {
		Vector vector = null;
		DBHelper helper = new DBHelper();
		long start1 = System.currentTimeMillis();
		vector = helper.query(SQL);
		long end1 = System.currentTimeMillis();
		System.out.println(end1 - start1);
		SQL += " ORDER BY NULL";
		long start2 = System.currentTimeMillis();
		vector = helper.query(SQL);
		long end2 = System.currentTimeMillis();
		System.out.println(end2 - start2);
	}

	@Test
	public void statementQuery() {
		String SQL = "FROM GAY_SQ WHERE gay_sq_sjhm ='15606710285'";
		DBHelper helper = new DBHelper();
		Vector vector = helper.query(SQL);
		printEntities(vector, new String[] { "GAY_SQ_BT" });
	}

}

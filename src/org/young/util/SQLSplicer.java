package org.young.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLSplicer {

	private String SQL;
	private StringBuffer buf;

	public SQLSplicer(String sql) {
		SQL = sql;
		buf = new StringBuffer();
	}

	public SQLSplicer addSelect() {
		if (SQL.startsWith("FROM") || SQL.startsWith("from")) {
			buf.append("SELECT").append(" * ");
		}
		buf.append(SQL);
		return this;
	}

	public SQLSplicer addOrder() {
		String regex = "(ORDER|order)[\\s+](BY|by)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(SQL);
		if (matcher.find()) {
			return this;
		}
		buf.append(" ORDER BY NULL");
		return this;
	}

	public String getSQL() {
		return buf.toString();
	}

	public static void main(String[] args) {
		String SQL = "SELECT NAME,PASSWORD FROM USER WHERE NAME='A' ORDER BY ID";
		SQLSplicer splicer = new SQLSplicer(SQL);
		splicer.addSelect().addOrder();
		System.out.println(splicer.getSQL());
	}
}

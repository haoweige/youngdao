package org.young.util;

import org.apache.commons.lang.StringUtils;

public class SQLGenerator {

	private StringBuffer buf;

	public SQLGenerator() {
		this.buf = new StringBuffer();
	}

	public SQLGenerator append(String SQL) {
		if (StringUtils.isNotEmpty(SQL)) {
			this.buf.append(SQL);
		}
		return this;
	}

	public SQLGenerator from(String table) {
		if (StringUtils.isNotEmpty(table)) {
			this.buf.append(" FROM ").append(table);
		}
		return this;
	}

	public SQLGenerator where() {
		this.buf.append(" WHERE ");
		return this;
	}

	public SQLGenerator where(String condition) {
		if (StringUtils.isNotEmpty(condition)) {
			this.buf.append(" WHERE ").append(condition);
		}
		return this;
	}

	public SQLGenerator and() {
		this.buf.append(" AND ");
		return this;
	}

	public SQLGenerator and(String condition) {
		if (StringUtils.isNotEmpty(condition)) {
			this.buf.append(" AND ").append(condition);
		}
		return this;
	}

	public SQLGenerator or() {
		this.buf.append(" OR ");
		return this;
	}

	public SQLGenerator or(String condition) {
		if (StringUtils.isNotEmpty(condition)) {
			this.buf.append(" OR ").append(condition);
		}
		return this;
	}

	public SQLGenerator condition(String column, String condition, String value) {
		if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(condition)
				&& StringUtils.isNotEmpty(value)) {
			this.buf.append(" ").append(column);
			this.buf.append(" ").append(condition);
			this.buf.append(" ").append(value);
		}
		return this;
	}

	public SQLGenerator groupBy(String column) {
		if (StringUtils.isNotEmpty(column)) {
			this.buf.append(" GROUP BY ").append(column);
		}
		return this;
	}

	public SQLGenerator orderBy(String column) {
		if (StringUtils.isNotEmpty(column)) {
			this.buf.append(" ORDER BY ").append(column);
		}
		return this;
	}

	public SQLGenerator orderBy(String[] columns) {
		if (columns != null && columns.length > 0) {
			this.buf.append(" ORDER BY ");
			for (int i = 0; i < columns.length; i++) {
				if (StringUtils.isNotEmpty(columns[i])) {
					this.buf.append(columns[i]).append(",");
				}
			}
			this.buf.deleteCharAt(buf.length());
		}
		return this;
	}

	public String getSQL() {
		return this.buf.toString();
	}

	public void test() {
		SQLGenerator generator = new SQLGenerator();
		generator.from("USER");
		generator.where();
		generator.condition("USERNAME", "=", "'docadmin'");
		generator.and();
		generator.condition("PASSWORD", "=", "'passw0rd'");
		System.out.println(generator.getSQL());
	}

}

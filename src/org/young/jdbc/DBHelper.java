package org.young.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.young.util.SQLSplicer;

import youngPackage.model.YoungContent;
import youngPackage.model.YoungProperty;

@SuppressWarnings("unchecked")
public class DBHelper {

	private static Logger logger = Logger.getLogger(DBHelper.class);

	// private ConnectionPool pool;
	// private C3P0Pool pool;
	private ProxoolPool pool;

	public DBHelper() {
		pool = ProxoolPool.getInstance();
	}

	private Connection getConnection() throws SQLException,
			InterruptedException {
		return pool.getConnection();
	}

	public Vector query(String SQL, Object[] values) {
		if (StringUtils.isEmpty(SQL))
			return null;
		SQLSplicer splicer = new SQLSplicer(SQL);
		splicer.addSelect().addOrder();
		SQL = splicer.getSQL();
		Vector vector = null;
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(SQL);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					statement.setObject(i + 1, values[i]);
				}
			}
			ResultSet resultSet = statement.executeQuery();
			vector = convert(resultSet);
			statement.close();
			connection.close();
		} catch (SQLException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		return vector;
	}

	public Vector<YoungContent> query(String SQL) {
		if (StringUtils.isEmpty(SQL))
			return null;
		SQLSplicer splicer = new SQLSplicer(SQL);
		splicer.addSelect().addOrder();
		SQL = splicer.getSQL();
		Vector<YoungContent> vector = null;
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(SQL);
			ResultSet resultSet = statement.executeQuery();
			vector = convert(resultSet);
			statement.close();
			connection.close();
		} catch (SQLException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		return vector;
	}

	public YoungContent queryByID(String table, String ID) {
		if (StringUtils.isEmpty(table) || StringUtils.isEmpty(ID))
			return null;
		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT *");
		buf.append(" FROM ").append(table);
		buf.append(" WHERE ");
		buf.append(" CONTENTID = '").append(ID).append("'");
		String SQL = buf.toString();
		Vector<YoungContent> vector = null;
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(SQL);
			ResultSet resultSet = statement.executeQuery();
			vector = convert(resultSet);
			statement.close();
			connection.close();
		} catch (SQLException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		if (vector != null && vector.size() > 0)
			return (YoungContent) vector.get(0);
		return null;
	}

	private Vector<YoungContent> convert(ResultSet resultSet)
			throws SQLException {
		Vector<YoungContent> vector = new Vector<YoungContent>();
		List<String> columns = parseColumns(resultSet);
		while (resultSet.next()) {
			YoungContent entity = new YoungContent();
			for (int i = 0; i < columns.size(); i++) {
				String column = columns.get(i);
				Object value = resultSet.getObject(column);
				if ("CONTENTID".equals(column))
					entity.setContentId(String.valueOf(value));
				// if (value != null) {
				YoungProperty property = new YoungProperty();
				property.setName(column);
				property.setValue(value);
				entity.addProperty(property);
				// }
			}
			vector.add(entity);
		}
		return vector;
	}

	private List<String> parseColumns(ResultSet resultSet) throws SQLException {
		List<String> columns = new LinkedList<String>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int count = metaData.getColumnCount();
		for (int i = 0; i < count; i++) {
			String column = metaData.getColumnName(i + 1);
			if (StringUtils.isNotEmpty(column))
				columns.add(column);
		}
		return columns;
	}
}

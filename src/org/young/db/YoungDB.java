package org.young.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import youngPackage.db.YoungRepository;
import youngPackage.exception.YoungException;
import youngPackage.model.YoungContent;
import youngPackage.model.YoungProperty;
import youngPackage.model.YoungSearch;

@SuppressWarnings("unchecked")
@Deprecated
public class YoungDB {

	private static Logger logger = Logger.getLogger(YoungDB.class);

	public Vector query(String SQL) {
		if (StringUtils.isEmpty(SQL))
			return null;
		Vector vector = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			YoungRepository connection = pool.getConnection();
			ResultSet resultSet = connection.executeQuery(SQL);
			vector = convert(resultSet);
			resultSet.close();
			pool.releaseConnection(connection);
		} catch (YoungException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		return vector;
	}

	public Vector search(String SQL) {
		if (StringUtils.isEmpty(SQL))
			return null;
		Vector vector = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			YoungRepository connection = pool.getConnection();
			YoungSearch search = new YoungSearch();
			search.setQueryString(SQL);
			search.setContentTypeName("");
			vector = connection.search(search);
			pool.releaseConnection(connection);
		} catch (YoungException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		return vector;
	}

	public YoungContent searchById(String table, String ID) {
		StringBuffer SQL = new StringBuffer();
		SQL.append(" FROM ").append(table);
		SQL.append(" WHERE ");
		SQL.append(" CONTENTID = '").append(ID).append("'");
		Vector vector = search(SQL.toString());
		if (vector != null && vector.size() > 0)
			return (YoungContent) vector.get(0);
		return null;
	}

	private Vector convert(ResultSet resultSet) throws SQLException {
		Vector vector = new Vector();
		List<String> columns = parseColumns(resultSet);
		while (resultSet.next()) {
			YoungContent youngContent = new YoungContent();
			for (int i = 0; i < columns.size(); i++) {
				String column = columns.get(i);
				Object value = resultSet.getObject(column);
				if ("CONTENTID".equals(column))
					youngContent.setContentId(String.valueOf(value));
				// if (value != null) {
				YoungProperty property = new YoungProperty();
				property.setName(column);
				property.setValue(value);
				youngContent.addProperty(property);
				// }
			}
			vector.add(youngContent);
		}
		return vector;
	}

	private List<String> parseColumns(ResultSet resultSet) throws SQLException {
		List<String> columns = new ArrayList<String>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int count = metaData.getColumnCount();
		for (int i = 0; i < count; i++) {
			columns.add(metaData.getColumnName(i + 1));
		}
		return columns;
	}

}

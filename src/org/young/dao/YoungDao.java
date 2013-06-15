package org.young.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.young.db.ConnectionPool;

import youngPackage.db.YoungRepository;
import youngPackage.exception.YoungException;
import youngPackage.model.YoungContent;
import youngPackage.model.YoungProperty;
import youngPackage.model.YoungSearch;

/**
 * The generic dao for youngdao. Pay more attention to calling the method
 * org.young.db.ConnectionPool.releaseConnection(YoungRepository repository)
 * 
 * 
 * @author haoweige@126.com
 */
@SuppressWarnings("unchecked")
public abstract class YoungDao implements IDao {

	private static Logger logger = Logger.getLogger(YoungDao.class);

	protected ConnectionPool pool;

	public YoungDao() {
		pool = ConnectionPool.getInstance();
	}

	@Override
	public void merge(YoungContent youngContent) {
		try {
			YoungRepository repository = pool.getConnection();
			repository.checkIn(youngContent, false);
			repository.merge(youngContent);
			repository.checkOut(youngContent, false);
			pool.releaseConnection(repository);
		} catch (YoungException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void persist(YoungContent youngContent) {
		try {
			YoungRepository repository = pool.getConnection();
			repository.persist(youngContent);
			pool.releaseConnection(repository);
		} catch (YoungException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void remove(YoungContent youngContent) {
		try {
			YoungRepository repository = pool.getConnection();
			repository.remove(youngContent);
			pool.releaseConnection(repository);
		} catch (YoungException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private YoungSearch getYoungSearch(String SQL) {
		if (StringUtils.isEmpty(SQL))
			SQL = " FROM " + getTable();
		YoungSearch search = new YoungSearch();
		search.setContentTypeName(getTable());
		search.setQueryString(SQL);
		return search;
	}

	@Override
	public Vector search(String SQL) {
		Vector result = null;
		try {
			YoungRepository repository = pool.getConnection();
			result = repository.search(getYoungSearch(SQL));
			pool.releaseConnection(repository);
		} catch (YoungException e) {
			logger.error(e);
			return null;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	@Override
	public Integer count(String SQL) {
		if (StringUtils.isEmpty(SQL))
			SQL = " FROM " + getTable();
		Integer result = 0;
		try {
			YoungRepository repository = pool.getConnection();
			result = repository.executeCount(getYoungSearch(SQL));
			pool.releaseConnection(repository);
		} catch (YoungException e) {
			logger.error(e);
			return 0;
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}
		return result;
	}

	@Override
	public Object searchById(String ID) {
		StringBuffer SQL = new StringBuffer();
		SQL.append(" FROM ").append(getTable());
		SQL.append(" WHERE ");
		SQL.append(" CONTENTID = '").append(ID).append("'");
		Vector vector = search(SQL.toString());
		if (vector != null && vector.size() > 0)
			return vector.get(0);
		return null;
	}

	@Override
	public <E> E query(String SQL, EntityConverter<E> convertor) {
		if (StringUtils.isEmpty(SQL))
			SQL = " FROM " + getTable();
		E entity = null;
		try {
			YoungRepository repository = pool.getConnection();
			ResultSet resultSet = repository.executeQuery(SQL);
			if (convertor != null)
				entity = convertor.convert(resultSet);
			resultSet.close();// close stream
			pool.releaseConnection(repository);// close connection
		} catch (YoungException e) {
			logger.error(e);
			return null;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		return entity;
	}

	@Override
	@Deprecated
	public Vector query(String SQL) {
		if (StringUtils.isEmpty(SQL))
			SQL = " FROM " + getTable();
		Vector<YoungContent> vector = null;
		try {
			YoungRepository repository = pool.getConnection();
			ResultSet resultSet = repository.executeQuery(SQL);
			vector = new YoungConverter().convert(resultSet);
			resultSet.close();// close stream
			pool.releaseConnection(repository);// close connection
		} catch (YoungException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		return vector;
	}

	class YoungConverter implements EntityConverter<Vector<YoungContent>> {

		@Override
		public Vector<YoungContent> convert(ResultSet resultSet)
				throws SQLException {
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

		private List<String> parseColumns(ResultSet resultSet)
				throws SQLException {
			List<String> columns = new ArrayList<String>();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int count = metaData.getColumnCount();
			for (int i = 0; i < count; i++) {
				columns.add(metaData.getColumnName(i + 1));
			}
			return columns;
		}

	}

}

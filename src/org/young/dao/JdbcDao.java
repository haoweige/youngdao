package org.young.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.young.jdbc.ProxoolPool;
import org.young.util.SQLSplicer;

import youngPackage.model.YoungContent;

@SuppressWarnings("unchecked")
public abstract class JdbcDao implements IDao {

	private static Logger logger = Logger.getLogger(JdbcDao.class);

	// protected ConnectionPool pool;
	// private C3P0Pool pool;
	private ProxoolPool pool;

	public JdbcDao() {
		pool = ProxoolPool.getInstance();
	}

	protected Connection getConnection() throws SQLException,
			InterruptedException {
		return pool.getConnection();
	}

	protected void releaseConnection(Connection connection) throws SQLException {
		pool.releaseConnection(connection);
	}

	@Override
	public <E> E query(String SQL, EntityConverter<E> convertor) {
		if (StringUtils.isEmpty(SQL))
			return null;
		SQLSplicer splicer = new SQLSplicer(SQL);
		splicer.addSelect().addOrder();
		SQL = splicer.getSQL();
		logger.info(SQL);
		E entity = null;
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(SQL);
			ResultSet resultSet = statement.executeQuery();
			entity = convertor.convert(resultSet);
			statement.close();
			resultSet.close();
			releaseConnection(connection);
		} catch (SQLException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		return entity;
	}

	public <E extends Object> E query(String SQL, Object[] conditions,
			EntityConverter<E> convertor) {
		if (StringUtils.isEmpty(SQL))
			return null;
		SQLSplicer splicer = new SQLSplicer(SQL);
		splicer.addSelect().addOrder();
		SQL = splicer.getSQL();
		logger.info(SQL + joinConditions(conditions));
		E entity = null;
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(SQL);
			for (int i = 0; i < conditions.length; i++) {
				statement.setObject(i + 1, conditions[i]);
			}
			ResultSet resultSet = statement.executeQuery();
			entity = convertor.convert(resultSet);
			statement.close();
			resultSet.close();
			releaseConnection(connection);
		} catch (SQLException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
		return entity;
	}

	protected String joinConditions(Object[] conditions) {
		StringBuffer buf = new StringBuffer();
		buf.append("- Conditions: ");
		for (int i = 0; i < conditions.length; i++) {
			buf.append(conditions[i]);
			if (i < conditions.length - 1)
				buf.append(",");
		}
		return buf.toString();
	}

	@Override
	public Vector query(String SQL) {
		// no need
		return null;
	}

	@Override
	public Integer count(String SQL) {
		// need
		return null;
	}

	@Override
	public void merge(YoungContent youngContent) {
		// no need
	}

	@Override
	public void persist(YoungContent youngContent) {
		// no need
	}

	@Override
	public void remove(YoungContent youngContent) {
		// no need
	}

	@Override
	public Vector search(String SQL) {
		// no need
		return null;
	}

	@Override
	public Object searchById(String ID) {
		// no need
		return null;
	}
}

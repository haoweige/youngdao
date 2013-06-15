package org.young.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The concrete factory for dao.Singleton Pattern.
 * 
 * @author haoweige@126.com
 */
@SuppressWarnings({ "unchecked", "deprecation" })
@Deprecated
public class DaoFactory implements IDaoFactory {

	private Logger logger = Logger.getLogger(DaoFactory.class);

	private static DaoFactory factory;

	private Map<String, YoungDao> youngMap;
	private Map<String, JdbcDao> jdbcMap;

	private DaoFactory() {
		youngMap = new HashMap<String, YoungDao>();
		jdbcMap = new HashMap<String, JdbcDao>();
	}

	public synchronized static DaoFactory buildFactory() {
		if (factory == null)
			factory = new DaoFactory();
		return factory;
	}

	@Override
	public <T extends YoungDao> T makeDao(Class<T> clazz) {
		T t = null;
		String name = clazz.getName();
		YoungDao dao = youngMap.get(name);
		try {
			if (dao == null) {
				t = (T) clazz.newInstance();
				youngMap.put(name, t);
				return t;
			}
		} catch (IllegalAccessException e) {
			logger.error(e);
			return null;
		} catch (InstantiationException e) {
			logger.error(e);
			return null;
		}
		t = (T) dao;
		return t;
	}

	@Override
	public <T extends JdbcDao> T makeDao_(Class<T> clazz) {
		T t = null;
		String name = clazz.getName();
		JdbcDao dao = jdbcMap.get(name);
		try {
			if (dao == null) {
				t = (T) clazz.newInstance();
				jdbcMap.put(name, t);
				return t;
			}
		} catch (IllegalAccessException e) {
			logger.error(e);
			return null;
		} catch (InstantiationException e) {
			logger.error(e);
			return null;
		}
		t = (T) dao;
		return t;
	}

}

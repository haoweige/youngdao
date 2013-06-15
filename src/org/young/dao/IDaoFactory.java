package org.young.dao;

/**
 * The abstract factory for dao
 * 
 * @author haoweige@126.com
 */
@Deprecated
public interface IDaoFactory {

	public <T extends YoungDao> T makeDao(Class<T> clazz);

	public <T extends JdbcDao> T makeDao_(Class<T> clazz);
}

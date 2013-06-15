package org.young.dao;

import java.util.Vector;

import youngPackage.model.YoungContent;

/**
 * The rule for naming table: all the letters is uppercase and separator of
 * words is _
 * 
 * @author haoweige@126.com
 */
@SuppressWarnings("unchecked")
public interface IDao {

	public void persist(YoungContent youngContent);

	public void merge(YoungContent youngContent);

	public void remove(YoungContent youngContent);

	public Vector search(String SQL);

	public Object searchById(String ID);

	public Integer count(String SQL);

	public <E extends Object> E query(String SQL, EntityConverter<E> convertor);

	@Deprecated
	public Vector query(String SQL);

	/**
	 * The rule of naming table: all the letters are uppercase and the separator
	 * of words is _
	 * 
	 * @return
	 */
	public String getTable();

}

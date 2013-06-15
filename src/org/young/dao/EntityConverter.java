package org.young.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EntityConverter<E extends Object> {

	public E convert(ResultSet resultSet) throws SQLException;
}

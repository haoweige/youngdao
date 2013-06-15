package org.young.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.young.util.PropertyReader;

public class ConnectionPool {

	private static Logger logger = Logger.getLogger(ConnectionPool.class);

	static final String DRIVER;
	static final String URL;
	static final String USERNAME;
	static final String PASSWORD;
	static final int IDLE_MIN;
	static final int IDLE_MAX;
	static final int CAPACITY;

	static {
		PropertyReader reader = new PropertyReader();
		DRIVER = reader.getString("db.driver");
		URL = reader.getString("db.url");
		USERNAME = reader.getString("db.username");
		PASSWORD = reader.getString("db.password");
		CAPACITY = reader.getInt("pool.capacity");
		IDLE_MAX = reader.getInt("pool.idlemax");
		IDLE_MIN = reader.getInt("pool.idlemin");
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			logger.info(e);
		}
	}

	private static ConnectionPool helper;
	private Vector<Connection> idleConnections;

	private int used;
	private int size;

	private ConnectionPool() {
		idleConnections = new Vector<Connection>();
	}

	public static synchronized ConnectionPool getInstance() {
		if (helper == null) {
			helper = new ConnectionPool();
		}
		return helper;
	}

	public synchronized void releaseConnection(Connection connection)
			throws SQLException {
		idleConnections.addElement(connection);
		used--;
		notifyAll();
		releaseConnection();
	}

	private synchronized void releaseConnection() throws SQLException {
		if (idleConnections.size() > IDLE_MAX) {
			Connection connection = idleConnections.firstElement();
			idleConnections.removeElementAt(0);
			close(connection);
		}
	}

	public synchronized Connection getConnection() throws SQLException,
			InterruptedException {
		Connection connection = null;
		if (idleConnections.size() > IDLE_MIN) {
			connection = idleConnections.firstElement();
			idleConnections.removeElementAt(0);
			if (connection.isClosed()) {
				connection = getConnection();
			}
			used++;
			return connection;
		}
		if (size > CAPACITY) {
			this.wait();
			return getConnection();
		}
		used++;
		Thread.sleep(50);
		return connect();
	}

	public synchronized void destroy() throws SQLException {
		Iterator<Connection> iterator = idleConnections.iterator();
		while (iterator.hasNext()) {
			Connection connection = iterator.next();
			if (!connection.isClosed()) {
				close(connection);
			}
			iterator.remove();
		}
	}

	private synchronized Connection connect() throws SQLException {
		Connection connection = null;
		connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		size++;
		return connection;
	}

	private synchronized void close(Connection connection) throws SQLException {
		connection.close();
		size--;
	}

	public synchronized int getSize() {
		return size;
	}

	public synchronized int getUsed() {
		return used;
	}

}

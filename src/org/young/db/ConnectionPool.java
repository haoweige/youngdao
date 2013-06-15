package org.young.db;

import java.util.Iterator;
import java.util.Vector;

import org.young.util.PropertyReader;

import youngPackage.db.YoungRepository;
import youngPackage.exception.YoungException;

public class ConnectionPool {

	static final String USERNAME;
	static final String PASSWORD;
	static final int IDLE_MIN;
	static final int IDLE_MAX;
	static final int CAPACITY;

	static {
		PropertyReader reader = new PropertyReader();
		USERNAME = reader.getString("db.username");
		PASSWORD = reader.getString("db.password");
		CAPACITY = reader.getInt("pool.capacity");
		IDLE_MAX = reader.getInt("pool.idlemax");
		IDLE_MIN = reader.getInt("pool.idlemin");
	}

	private static ConnectionPool pool;
	private Vector<YoungRepository> idleRepositorys;

	private int used;
	private int size;

	private ConnectionPool() {
		idleRepositorys = new Vector<YoungRepository>();
	}

	public static synchronized ConnectionPool getInstance() {
		if (pool == null) {
			pool = new ConnectionPool();
		}
		return pool;
	}

	public synchronized void releaseConnection(YoungRepository youngRepository)
			throws YoungException {
		idleRepositorys.addElement(youngRepository);
		used--;
		notifyAll();
		releaseConnection();
	}

	private synchronized void releaseConnection() throws YoungException {
		if (idleRepositorys.size() > IDLE_MAX) {
			YoungRepository youngRepository = idleRepositorys.firstElement();
			idleRepositorys.removeElementAt(0);
			close(youngRepository);
		}
	}

	public synchronized YoungRepository getConnection() throws YoungException,
			Exception {
		YoungRepository youngRepository = null;
		if (idleRepositorys.size() > IDLE_MIN) {
			youngRepository = idleRepositorys.firstElement();
			idleRepositorys.removeElementAt(0);
			if (!youngRepository.isConnected()) {
				youngRepository = getConnection();
			}
			used++;
			return youngRepository;
		}
		if (size > CAPACITY) {
			this.wait();
			return getConnection();
		}
		used++;
		Thread.sleep(50);
		return connect();
	}

	public synchronized void destroy() throws YoungException {
		Iterator<YoungRepository> iterator = idleRepositorys.iterator();
		while (iterator.hasNext()) {
			YoungRepository youngRepository = iterator.next();
			if (youngRepository.isConnected()) {
				close(youngRepository);
			}
			iterator.remove();
		}
	}

	private synchronized YoungRepository connect() throws YoungException,
			Exception {
		YoungRepository repository = new YoungRepository();
		repository.connect();
		repository.logon(USERNAME, PASSWORD);
		size++;
		return repository;
	}

	private synchronized void close(YoungRepository youngRepository)
			throws YoungException {
		youngRepository.logoff();
		youngRepository.disconnect();
		size--;
	}

	public synchronized int getSize() {
		return size;
	}

	public synchronized int getUsed() {
		return used;
	}

}

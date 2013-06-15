package org.young.util;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class PropertyReader {

	private static Logger logger = Logger.getLogger(PropertyReader.class);

	private CompositeConfiguration config;

	public PropertyReader(String path) {
		config = new CompositeConfiguration();
		try {
			PropertiesConfiguration properties = new PropertiesConfiguration(
					path);
			config.addConfiguration(properties);
		} catch (ConfigurationException e) {
			logger.error(e);
		}
	}

	public PropertyReader() {
		this("db.properties");
	}

	public String getString(String name) {
		return config.getString(name);
	}

	public int getInt(String name) {
		return config.getInt(name);
	}

}

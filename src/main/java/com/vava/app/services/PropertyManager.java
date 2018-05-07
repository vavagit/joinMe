package com.vava.app.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PropertyManager {
	private static final String CONFIG_PATH = "src/main/resources/startupConfiguration.properties";
	
	private Properties properties = new Properties();
	
    private Logger logger = LogManager.getLogger(EventManagerService.class);

	
	public PropertyManager() {
		properties = loadFromFile();
	}
	
	private Properties loadFromFile() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(CONFIG_PATH)));
		}
		catch(FileNotFoundException e) {
			logger.catching(Level.WARN, e);
			e.printStackTrace();
		}
		catch (IOException e) {
			logger.catching(Level.ERROR, e);
			e.printStackTrace();
		}
		return properties;
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key, "");
	}
}

package com.vava.app.model.database;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Vytvorenie datoveho zdroja k databaze. Pre pristup k databaze sa pouziva spring JDBC template
 * 
 * @author erikubuntu
 */
public class DatabaseManager {
	private static final String CONFIG_PATH = "classpath:/beans/database_configuration.xml";
	private JdbcTemplate connection;
	//dummy ORM access
	
	public DatabaseManager() {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_PATH);
		DataSource source = context.getBean("dataSource", DriverManagerDataSource.class);
		connection = new JdbcTemplate(source);
		
		
	}

}

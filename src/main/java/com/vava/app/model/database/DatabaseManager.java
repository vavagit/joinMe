package com.vava.app.model.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.vava.app.model.User;

/**
 * Vytvorenie datoveho zdroja k databaze. Pre pristup k databaze sa pouziva spring JDBC template
 * 
 * @author erikubuntu
 */
@Component
public class DatabaseManager {
	private static final String CONFIG_PATH = "classpath:/beans/database_configuration.xml";
	private JdbcTemplate connection;
	
	public DatabaseManager() {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_PATH);
		DataSource source = context.getBean("dataSource", DriverManagerDataSource.class);
		connection = new JdbcTemplate(source);
		((ConfigurableApplicationContext)context ).close();
	}
	
	public User getUserByEmail(String email) {
		List<User> users = connection.query("SELECT * FROM users WHERE email = ?",new Object[] {email}, new BeanPropertyRowMapper<User>(User.class));
		if(users ==  null || users.isEmpty()) {
			return null;
		}
		return users.get(0);
	}

	public boolean createUser(User user) {
		PreparedStatementSetter setter = new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, user.getEmail());
				ps.setString(2, user.getPassword());
			}
		};
		try {
			connection.update("INSERT INTO users VALUES(DEFAULT,?,?)", setter);
			return true;
		}catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}
}

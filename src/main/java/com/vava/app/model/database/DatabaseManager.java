package com.vava.app.model.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
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
	private static final String CONFIG_PATH = "classpath:/beans/databaseConfiguration.xml";
	private JdbcTemplate connection;
	
	public DatabaseManager() {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_PATH);
		DataSource source = context.getBean("dataSource", DriverManagerDataSource.class);
		connection = new JdbcTemplate(source);
		((ConfigurableApplicationContext)context ).close();
	}
	
	/**
	 * Najdenie uzivatela podla jeho uzivatelskeho mena. Metoda uvazuje ze 2 uzivatelia nemaju
	 * rovnake prihlasovacie meno.
	 * @param userName hladane prihlasovacie meno
	 * @return Naplneny objekt {@link User}
	 */
	public User getUserByUserName(String userName) {
		List<User> users = connection.query("SELECT * FROM users WHERE user_name = ?",new Object[] {userName}, new UserRowMapper());
		if(users ==  null || users.isEmpty()) {
			return null;
		}
		return users.get(0);
	}

	public User createUser(User user) {
		//vytvorenie preparedSteatmmentu
		PreparedStatementSetter setter = new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, user.getUserName());
				ps.setString(2, user.getPassword());
				ps.setDate(3, user.getRegisteredAt());
				ps.setString(4, user.getName());
				ps.setString(5, user.getLastName());
				ps.setDouble(6, user.getRating());
				ps.setString(7, user.getContact());
				ps.setString(8, user.getAddress());
				ps.setString(9, user.getAddressLocation().getLongitude() + "");
				ps.setString(10, user.getAddressLocation().getLatitude() + "");
			}
		};
		//ak uzivatelove pouzivatelske meno uz existuje vrat neuspech
		if(getUserByUserName(user.getUserName()) != null)
			return null;
		
		//vytvorenie uzivatela - insert do databazy
		try {
			
			connection.update("INSERT INTO users VALUES(DEFAULT,?,?,?,?,?,?,?,?,?,?)", setter);
			return getUserByUserName(user.getUserName());
		}catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean removeUser(String username) {
		User user = getUserByUserName(username);
		if(user == null)
			return false;
		//vymazanie eventov uzivatela
		connection.update("DELETE FROM events WHERE user_id_creator = ?", new Object[] {user.getId()});
		//vymazanie uzivatela z prihlasenych eventov
		connection.update("DELETE FROM joined_users WHERE id_user = ?", new Object[] {user.getId()});
		int affected = connection.update("DELETE FROM users WHERE users.user_name = ?", new Object[] {username});
		System.out.println("remove user Debug:" + affected);
		return affected != 0;
	}
	
	public void getUsersEvents(int userId) {
		connection.query("SELECT events.*, category.sport, category.id as category_id FROM events JOIN category ON category.id = sport_category_id WHERE user_name = ?",new Object[] {userId}, new UserRowMapper());
	} 
}

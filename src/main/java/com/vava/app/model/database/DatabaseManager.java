package com.vava.app.model.database;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.vava.app.model.Event;
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
		//ak uzivatelove pouzivatelske meno uz existuje vrat neuspech
		if(getUserByUserName(user.getUserName()) != null)
			return null;
		
		//vytvorenie uzivatela - insert do databazy
		try {
			
			connection.update("INSERT INTO users VALUES(DEFAULT,?,?,?,?,?,?,?,?,?,?)", new UserStatementSetter(user));
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
	
	public List<Event> getUsersEvents(int userId) {
		String query = "SELECT  events.*, c2.sport FROM events "
				+ "JOIN joined_users u ON events.id = u.id_event "
				+ "JOIN category c2 ON events.sport_category_id = c2.id "
				+ "WHERE u.id_user = ?";
		return connection.query(query,new Object[] {userId}, new EventRowMapper());
	}
	
	public List<Event> getEventsCreatedByUser(int userId){
		String query = "SELECT * FROM events "
					+ "JOIN category c2 ON events.sport_category_id = c2.id "
					+ "WHERE events.user_id_creator = ?";
		return connection.query(query, new Object[] {userId}, new EventRowMapper());
	}
	
	public User getUserDetails(int userId) {
		String query = "SELECT * FROM users WHERE id = ?;";
		List<User> users = connection.query(query, new Object[] {userId}, new UserRowMapper());			
		if(users.isEmpty())
			return null;
		User user = users.get(0);
		user.setPassword("");
		return user;
	}
	
	public boolean createEvent(Event newEvent){
		int affected;
		try {
			affected = connection.update("INSERT INTO events VALUES(DEFAULT,?,?,?,?,?,?,?,?,?,?)", new EventStatementSetter(newEvent));
		} catch(DataAccessException exception) {
			exception.printStackTrace();
			return false;
		}
		return affected > 0;
	}
	
	public boolean removeEvent(int eventId) {
		int affected = connection.update("DELETE FROM events WHERE events.id = ?", new Object[] {eventId});
		return affected > 0;
	}
}

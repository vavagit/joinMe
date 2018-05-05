package com.vava.app.model.database;

import java.sql.Date;
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
		//vymazanie uzivatelov hlasiacich sa na event
		connection.update("DELETE FROM joined_users WHERE id_event = ?", new Object[] {eventId});
		int affected = connection.update("DELETE FROM events WHERE events.id = ?", new Object[] {eventId});
		return affected > 0;
	}
	
	public Event getEventDetails(int eventId) {
		String query = "SELECT events.*, c2.sport FROM events JOIN category c2 ON events.sport_category_id = c2.id WHERE events.id = ?;";
		List<Event> events = connection.query( query, new Object[] {eventId}, new EventRowMapper());
		if(events.isEmpty())
			return null;
		return events.get(0);
	}
	
	public boolean addUserToEvent(int userId, int eventId) {
		//kontrola ci sa uzivatel na event uz neprihlasil
		int rows = connection.queryForObject("SELECT COUNT(*) FROM joined_users WHERE id_event = ? AND id_user = ?", new Object[] {eventId, userId}, Integer.class);
		if(rows > 0)
			return false;
		//kontrola naplnenia eventu
		//vratenie poctu prihlasenych uzivatelov
		int used = connection.queryForObject("SELECT COUNT(*) FROM joined_users WHERE id_event = ?", new Object[] {eventId}, Integer.class);
		int max = connection.queryForObject("SELECT max_users FROM events WHERE id = ?", new Object[] {eventId}, Integer.class);
		//ak je event plny vrat neuspech
		if(max <= used) 
			return false;
		
		java.sql.Date date = new Date(new java.util.Date().getTime());
		int affected = 0;
		try {
			String query = "INSERT INTO joined_users (id, id_event, id_user, accepted_at) VALUES(DEFAULT,?,?,?)";
			affected = connection.update(query, new Object[] {eventId, userId, date});
		}catch(DataAccessException e) {
			e.printStackTrace();
			return false;
		}
		return affected > 0;
	}
	
	public boolean removeUserFromEvent(int userId, int eventId) {
		int affected = connection.update("DELETE FROM joined_users WHERE id_event = ? AND id_user = ?", new Object[] {eventId, userId});
		return affected > 0;
	}
	
	public void updateEventDetails(Event event) {
		String query = "UPDATE events SET name = ?, description = ?, max_users = ?," + 
				"  sport_category_id = ?, latitude = ?, longitude = ?, event_date = ?," + 
				"  necessary_age = ?, address = ? WHERE id = ?";
		
		PreparedStatementSetter setter = (ps) ->{
			ps.setString(1, event.getEventName());
			ps.setString(2, event.getDescription());
			ps.setInt(3, event.getMaxUsersOnEvent());
			ps.setInt(4, event.getSportCategory().getId());
			ps.setDouble(5, event.getEventLocation().getLatitude());
			ps.setDouble(6, event.getEventLocation().getLongitude());
			ps.setDate(7, event.getDate());
			ps.setInt(8, event.getNecessaryAge());
			ps.setString(9, event.getAddress());
			ps.setInt(10, event.getEventId());
		};
		
		try{
			int affected = connection.update(query, setter);
			System.out.println("DEBUG: updateEvent affected: " + affected);
		}catch(DataAccessException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Vrati vsetky eventy v rozsahu definovanom parametrami
	 * @param limit pocet prvkov ktory ma databaza vratit
	 * @param offset posun v tabulke databazy
	 * @return List {@link Event}
	 */
	public List<Event> getEvents(int limit, int offset){
		String query = "SELECT  events.*, c2.sport FROM events " + 
				"JOIN category c2 ON events.sport_category_id = c2.id " + 
				"LIMIT " + limit + "OFFSET " + offset;
		return connection.query(query, new EventRowMapper());
	}
}

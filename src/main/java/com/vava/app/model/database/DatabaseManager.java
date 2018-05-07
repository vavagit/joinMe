package com.vava.app.model.database;

import java.sql.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static Logger logger = LogManager.getLogger(DatabaseManager.class);

	public DatabaseManager() {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_PATH);
		DataSource source = context.getBean("dataSource", DriverManagerDataSource.class);
		connection = new JdbcTemplate(source);
		logger.info("Vytvorenie spojenia na databazu");
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
			logger.debug("getUserByUserName, uzivatel " + userName + " nenajdeny");
			return null;
		}
		logger.debug("getUserByUserName, uzivatel " + userName + " najdeny");
		return users.get(0);
	}

	public User createUser(User user) {
		//ak uzivatelove pouzivatelske meno uz existuje vrat neuspech
		if(getUserByUserName(user.getUserName()) != null) {
			logger.debug("CreateUser, Uzivatelske meno " + user.getName() + " uz existuje");
			return null;
		}
		
		//vytvorenie uzivatela - insert do databazy
		try {
			connection.update("INSERT INTO users VALUES(DEFAULT,?,?,?,?,?,?,?,?,?,?)", new UserStatementSetter(user));
			logger.debug("CreateUser, Pridanie uzivatela do databazy");
			return getUserByUserName(user.getUserName());
		}catch (DataAccessException e) {
			logger.catching(Level.ERROR,e);
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean removeUser(String username) {
		User user = getUserByUserName(username);
		if(user == null) {
			logger.debug("removeUser, uzivatel " + username + " nenajdeny");
			return false;
		}
		try {
			//vymazanie eventov uzivatela
			connection.update("DELETE FROM events WHERE user_id_creator = ?", new Object[] {user.getId()});
			logger.debug("removeUser, eventy uzivatela vymazane");
			//vymazanie uzivatela z prihlasenych eventov
			connection.update("DELETE FROM joined_users WHERE id_user = ?", new Object[] {user.getId()});
			logger.debug("removeUser, prihlasky uzivatela vymazane");
			int affected = connection.update("DELETE FROM users WHERE users.user_name = ?", new Object[] {username});
			logger.debug("removeUser, vymazanie eventu: " + (affected > 0) + " affected: " + affected);
			return affected != 0;
		} catch(DataAccessException exception) {
			exception.printStackTrace();
			logger.catching(Level.ERROR,exception);
			return false;
		}
	}
	
	public List<Event> getUsersEvents(int userId) {
		String query = "SELECT  events.*, c2.sport FROM events "
				+ "JOIN joined_users u ON events.id = u.id_event "
				+ "JOIN category c2 ON events.sport_category_id = c2.id "
				+ "WHERE u.id_user = ?";
		logger.debug("getUsersEvents, spustenie query");
		return connection.query(query,new Object[] {userId}, new EventRowMapper());
	}
	
	public List<Event> getEventsCreatedByUser(int userId){
		String query = "SELECT * FROM events "
					+ "JOIN category c2 ON events.sport_category_id = c2.id "
					+ "WHERE events.user_id_creator = ?";
		logger.debug("getEventsCreatedByUser, spustenie query");
		return connection.query(query, new Object[] {userId}, new EventRowMapper());
	}
	
	public User getUserDetails(int userId) {
		String query = "SELECT * FROM users WHERE id = ?;";
		logger.debug("getUserDetails, spustenie query");
		List<User> users = connection.query(query, new Object[] {userId}, new UserRowMapper());			
		if(users.isEmpty()) {
			logger.debug("getUserDetails, uzivatel " + userId + " nenajdeny");
			return null;
		}
		User user = users.get(0);
		user.setPassword("");
		logger.debug("getUserDetails, uzivatel " + userId + " najdeny");
		return user;
	}
	
	public boolean createEvent(Event newEvent){
		int affected;
		try {
			affected = connection.update("INSERT INTO events VALUES(DEFAULT,?,?,?,?,?,?,?,?,?,?)", new EventStatementSetter(newEvent));
		} catch(DataAccessException exception) {
			exception.printStackTrace();
			logger.catching(Level.ERROR,exception);
			return false;
		}
		logger.debug("CreateEvent, Pridanie eventu: " + (affected > 0) + " affected: " + affected);
		return affected > 0;
	}
	
	public boolean removeEvent(int eventId) {
		//vymazanie uzivatelov hlasiacich sa na event
		connection.update("DELETE FROM joined_users WHERE id_event = ?", new Object[] {eventId});
		int affected = 0;
		
		try {
			affected = connection.update("DELETE FROM events WHERE events.id = ?", new Object[] {eventId});
		} catch(DataAccessException exception) {
			exception.printStackTrace();
			logger.catching(Level.ERROR,exception);
			return false;
		}
		
		logger.debug("CreateEvent, odstranenie eventu: " + (affected > 0) + " affected: " + affected);
		return affected > 0;
	}
	
	public Event getEventDetails(int eventId) {
		String query = "SELECT events.*, c2.sport FROM events JOIN category c2 ON events.sport_category_id = c2.id WHERE events.id = ?;";
		List<Event> events = connection.query( query, new Object[] {eventId}, new EventRowMapper());
		if(events.isEmpty()) {
			logger.debug("getEventDetails, Event " + eventId + " nenajdeny");
			return null;
		}
		logger.debug("getEventDetails, Event " + eventId + " najdeny");
		return events.get(0);
	}
	
	public boolean addUserToEvent(int userId, int eventId) {
		//kontrola ci sa uzivatel na event uz neprihlasil
		int rows = connection.queryForObject("SELECT COUNT(*) FROM joined_users WHERE id_event = ? AND id_user = ?", new Object[] {eventId, userId}, Integer.class);
		if(rows > 0) {
			logger.debug("addUserToEvent, Uzivatel " + userId + " Event: " + eventId + ", kontrola prihlasky: Uz prihlaseny");
			return false;
		}
		logger.debug("addUserToEvent, Uzivatel " + userId + " Event: " + eventId + ", kontrola prihlasky: OK");
		//kontrola naplnenia eventu
		//vratenie poctu prihlasenych uzivatelov
		int used = connection.queryForObject("SELECT COUNT(*) FROM joined_users WHERE id_event = ?", new Object[] {eventId}, Integer.class);
		int max = connection.queryForObject("SELECT max_users FROM events WHERE id = ?", new Object[] {eventId}, Integer.class);
		//ak je event plny vrat neuspech
		if(max <= used) {
			logger.debug("addUserToEvent, Uzivatel " + userId + " Event: " + eventId + ", kontrola miesta: Plne");
			return false;
		}
		logger.debug("addUserToEvent, Uzivatel " + userId + ", kontrola miesta: Volne (" + (max - used) + ")");

		java.sql.Date date = new Date(new java.util.Date().getTime());
		int affected = 0;
		try {
			String query = "INSERT INTO joined_users (id, id_event, id_user, accepted_at) VALUES(DEFAULT,?,?,?)";
			affected = connection.update(query, new Object[] {eventId, userId, date});
			logger.debug("addUserToEvent, Uzivatel " + userId + " Event: " + eventId + ", pridany: " + (affected > 0));
		}catch(DataAccessException e) {
			e.printStackTrace();
			logger.catching(Level.ERROR, e);
			return false;
		}
		return affected > 0;
	}
	
	public boolean removeUserFromEvent(int userId, int eventId) {
		int affected = 0;
		try {
			affected = connection.update("DELETE FROM joined_users WHERE id_event = ? AND id_user = ?", new Object[] {eventId, userId});
			logger.debug("removeUserFromEvent, event: " + eventId + " uzivatel: " + userId + " odstraneny: " + (affected > 0));
		}catch (DataAccessException e) {
			e.printStackTrace();
			logger.catching(Level.ERROR, e);
		}
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
			logger.debug("updateEventDetails, Event: " + event.getEventId() + " upravene: " + (affected > 0) + " affected: " + affected);
		}catch(DataAccessException ex) {
			logger.catching(Level.ERROR, ex);
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
		logger.debug("getEvents, Spustenie query limit: " + limit + " offset: " + offset);
		return connection.query(query, new EventRowMapper());
	}
}

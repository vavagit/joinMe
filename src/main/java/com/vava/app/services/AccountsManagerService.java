package com.vava.app.services;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vava.app.model.Event;
import com.vava.app.model.User;
import com.vava.app.model.UserStatus;
import com.vava.app.model.database.DatabaseManager;
/**
 * Spravca prihlaseni uzivatelov.Tvori rozhranie na komunikaciu 
 * controllerov a databazy na cinnosti autorizacie a utentifikacie
 * ktore prebiehaju pripojenim na databazu a naslednou kontrolou 
 * existencie uzivatela.
 * @author erikubuntu
 *
 */
@Component
public class AccountsManagerService implements AccountService{
	@Autowired
	private DatabaseManager db;
	
    private Logger logger = LogManager.getLogger(AccountsManagerService.class);
 
	@Override
	public User createUser(User user) {
		User created = db.createUser(user);
		if(created == null) {
			logger.debug("createUser, Uzivatel nebol vytvoreny");
			return null;
		}
		created.setPassword("");
		created.setUserName("");
		logger.debug("createUser, Uzivatel " + created.getId() + "vytvoreny");
		return created;
	}
	
	@Override
	public User login(String userName, String password) {
		if(userName == null || password == null)
			return null;
		User user = findUserByUserName(userName);
		if(user == null) {
			logger.debug("login, Uzivatel " + userName + " nenajdeny");
			return null;
		}
		if(user.getPassword().equals(password)){
			user.setPassword("");
			user.setUserName("");
			logger.debug("login, Uzivatel " + userName + " autentifikacia uspesna");
			return user;
		}
		logger.debug("login, Uzivatel " + userName + " zle heslo");
		return null;
	}
	
	/**
	 * Funkcia sluzi na autorizaciu pri volani funkcii controllerov. 
	 * @param authorizationList enkodovany string obsahujuci meno a heslo uzivatela
	 * @return true ak je uzivatel prihlaseny a moze pristupovat k danej metode inak false
	 */
	public boolean authorization(List<String> authorizationList) {
		if(authorizationList == null) {
			logger.debug("authorization, Prazdny list autorizacie: neuspech");
			return false;
		}
		//prevod listu na string
		String authorizationString = "";
		for(String temp : authorizationList)
				authorizationString += temp;
		
		// dekodovanie prijateho autorizacneho stringu
		if (authorizationString.startsWith("Basic")) {

			String base64Credentials = authorizationString.substring("Basic".length()).trim();
	        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
	        								Charset.forName("UTF-8"));
	        logger.debug("authorization, dekodovanie");
	        final String[] values = credentials.split(":",2);
	        //kontrola existencie uzivatela
	        return login(values[0],values[1]) != null;
		}
        logger.debug("authorization, nerozpoznany format: " + authorizationString);
		return false;
	}

	/**
	 * Najdenie uzivatela v zdroji udajov podla mena. Naplnenie objektu uzivatela s
	 * jeho vsetkymi udajmi
	 * 
	 * @return {@link User} ak uzivatel existuje inak null
	 */
	public User findUserByUserName(String username) {
		if(username == null)
			return null;
		return db.getUserByUserName(username);
	}

	@Override
	public boolean removeUser(User user) {
		User found = findUserByUserName(user.getUserName());
		if(found == null) {
			logger.debug("removeUser, Pokus o odstranenie neexistujuceho uzivatela");
			return false;
		}
		logger.debug("removeUser, Uzivatel " + user.getId() + " odstraneny");
		return db.removeUser(user.getUserName());
	}

	@Override
	public User getUserDetails(int userId) {
		return db.getUserDetails(userId);
	}
	

	@Override
	public List<Event> getEventsCreatedByUser(int userId) {
		return db.getEventsCreatedByUser(userId);
	}

	@Override
	public List<Event> getUsersEvents(int userId) {
		return db.getUsersEvents(userId);
	}

	@Override
	public boolean applyToEvent(int userId, int eventId) {
		return db.addUserToEvent(userId, eventId);
	}
	
	@Override
	public boolean removeApplicationToEvent(int userId, int eventId) {
		return db.removeUserFromEvent(userId, eventId);
	}

	@Override
	public UserStatus getUserStatusToEvent(int userId, int eventId) {
		logger.debug("getUserStatusToEvent, zistenie vztahu medzi: EventId = " + eventId 
				+ " UserId = " + userId);
		if(db.isUserCreator(eventId, userId)) {
			logger.debug("getUserStatusToEvent, Uzivatel" + userId + " je tvorca");
			return UserStatus.CREATOR;
		}
		if(db.isUserJoined(eventId, userId)) {
			logger.debug("getUserStatusToEvent, Uzivatel" + userId + " je zaregistrovany");
			return UserStatus.JOINED;
		}
		logger.debug("getUserStatusToEvent, Uzivatel" + userId + " nie je zaregistrovany");
		return UserStatus.NONE;
	}

}

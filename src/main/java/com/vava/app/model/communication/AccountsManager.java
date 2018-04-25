package com.vava.app.model.communication;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vava.app.model.User;
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
public class AccountsManager {
	@Autowired
	private DatabaseManager db;
	
	/**
	 * Funkcia sluzi na autorizaciu pri volani funkcii controllerov. 
	 * @param authorizationList enkodovany string obsahujuci meno a heslo uzivatela
	 * @return true ak je uzivatel prihlaseny a moze pristupovat k danej metode inak false
	 */
	public boolean authorization(List<String> authorizationList) {
		if(authorizationList == null)
			return false;
		//prevod listu na string
		String authorizationString = "";
		for(String temp : authorizationList)
				authorizationString += temp;
		
		if (authorizationString.startsWith("Basic")) {
	        // Authorization: Basic base64credentials
	        String base64Credentials = authorizationString.substring("Basic".length()).trim();
	        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
	        								Charset.forName("UTF-8"));
		    // credentials = username:password
		    final String[] values = credentials.split(":",2);
		    System.out.println(values[0] + " " + values[1]);
		    return true;
		}
		return false;
	}
	
	public boolean isLogedIn(String username) {
		return true;
	}
	
	
	/**
	 * Funkcia vytvori zaznam v databaze o uzivatelovi.
	 * @param user {@link User} objekt uzivatela s naplnenymi udajmi
	 * @return false aj uzivatel existuje alebo nebol vytvoreny inak true
	 */
	public boolean createUser(User user) {
		
		return false;
	}
	
}

package com.vava.app.services;

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
public class AccountsManagerService implements AccountService{
	@Autowired
	private DatabaseManager db;
	
	/**
	 * Funkcia vytvori zaznam v databaze o uzivatelovi.
	 * @param user {@link User} objekt uzivatela s naplnenymi udajmi
	 * @return false aj uzivatel existuje alebo nebol vytvoreny inak true
	 */
	@Override
	public boolean createUser(User user) {
		return db.createUser(user);
	}

	@Override
	public boolean login(String email, String password) {
		return true;
	}
	
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

			String base64Credentials = authorizationString.substring("Basic".length()).trim();
	        String credentials = new String(Base64.getDecoder().decode(base64Credentials),
	        								Charset.forName("UTF-8"));

	        final String[] values = credentials.split(":",2);
	        User found = findUserByEmail(values[0]);
	        //uzivatel nebol najdeny
	        if(found == null)
	        	return false;
	        
	        return found.getPassword().equals(values[1]);
		}
		return false;
	}

	/**
	 * Najdenie uzivatela v zdroji udajov podla mena. Naplnenie objektu uzivatela s jeho vsetkymi udajmi
	 * @return {@link User} ak uzivatel existuje inak null
	 */
	@Override
	public User findUserByEmail(String email) {
		return db.getUserByEmail(email);
	}
	

}

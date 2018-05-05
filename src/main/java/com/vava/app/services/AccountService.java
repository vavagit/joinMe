package com.vava.app.services;

import java.util.List;

import com.vava.app.model.Event;
import com.vava.app.model.User;

public interface AccountService {
	/**
	 * Funkcia vytvori zaznam v databaze o uzivatelovi.
	 * 
	 * @param user
	 *            {@link User} objekt uzivatela s naplnenymi udajmi
	 * @return null ak uzivatel existuje alebo nebol vytvoreny inak odkaz na
	 *         vytvoreneho Usera
	 */
	public User createUser(User user);

	/**
	 * @return Ak sa uzivatel uspesne prihlasi objekt s vyplnenymi udajmi okrem mena
	 *         a hesla inak null
	 */
	public User login(String username, String password);

	/**
	 * Vyhladanie zaznamu v databaze o hladonom uzivatelovi
	 * 
	 * @param id
	 *            id hladaneho uzivatela
	 * @return {@link User} s vyplnenymi udajmi
	 */
	public User getUserDetails(int id);

	/**
	 * Odstranenie zaregistrovaneho uzivatela.
	 * 
	 * @return true ak sa uzivatela podarilo odstranit inak false
	 */
	public boolean removeUser(User user);

	/**
	 * Vyhlada eventy ktorych sa zucasnuje uzivatel
	 * 
	 * @param userId
	 *            kontrolovany uzivatel
	 * @return zoznam eventov
	 */
	public List<Event> getUsersEvents(int userId);

	/**
	 * Vrati vsetky eventy vytvorene uzivatelom
	 * 
	 * @param userId
	 *            id overovaneho uzivatela
	 * @return list hladanych eventov
	 */
	public List<Event> getEventsCreatedByUser(int userId);

	/**
	 * Prihlasenie uzivatela na event
	 * 
	 * @param userId
	 *            id uzavatela ktory ziada o prihlasenie
	 * @param eventId
	 *            id eventu na ktory sa uzivatel skusa prihlasit
	 * @return true ak prihlasenie prebehlo bez problemov inak false. Za neuspech sa
	 *         povazuje ak je event plny,uzivatel je uz prihlaseny alebo nastala ina
	 *         chyba
	 */
	public boolean applyToEvent(int userId, int eventId);

	/**
	 * Odstranenie prihlasenia uzivatela na event
	 * 
	 * @param userId
	 *            id uzivatela ktory sa pokusa odhlasit z eventu
	 * @param eventId
	 *            id eventu z ktoreho sa uzivatel pokusa odhlasit
	 * @return true ak sa podarilo uzivatela odhlasit inak false
	 */
	public boolean removeApplicationToEvent(int userId, int eventId);
}

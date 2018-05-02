package com.vava.app.services;

import java.util.List;

import com.vava.app.model.Event;
import com.vava.app.model.Location;

public interface EventService {
	/**
	 * vyhlada eventy nachadzajuce sa v v radiuse od zadanej pozicie
	 * @param kilometerRadius rozsah v ktorom prebieha hladanie
	 * @param location bod z ktoreho sa zacne vyhladavanie
	 * @return list eventov
	 */
	public List<Event> getEventsFromRange(int kilometerRadius, Location location);
	/**
	 * Vyhlada eventy ktorych sa zucasnuje uzivatel
	 * @param userId kontrolovany uzivatel
	 * @return zoznam eventov
	 */
	public List<Event> getUsersEvent(int userId);
	/**
	 * Vrati vsetky eventy vytvorene uzivatelom
	 * @param userId id overovaneho uzivatela
	 * @return list hladanych eventov
	 */
	public List<Event> getEventsCreatedByUser(int userId);
	
	/**
	 *
	 * @param eventId id eventu
	 * @return detaily o hladanom evente 
	 */
	public Event getEventDetails(int eventId);
	
	/**
	 * Vyhtovenie noveho eventu
	 * @param newEvent objekt eventu ktory a prida do databazy
	 * @return true ak sa podarilo event vytvorit
	 */
	public boolean createEvent(Event newEvent);
	
	/**
	 * Odstranenie eventu podla id
	 * @param eventId
	 */
	public void removeEvent(int eventId);
	
	/**
	 * Zmena vlastnosti eventu
	 * @param updatedEvent objekt eventu so zmenenymi udajmi
	 * @return true ak sa podarilo zmenit udaje inak false
	 */
	public boolean updateEventDetails(Event updatedEvent);
}

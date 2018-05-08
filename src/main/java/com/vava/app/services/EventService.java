package com.vava.app.services;

import java.util.List;

import com.vava.app.model.Event;
import com.vava.app.model.Location;
import com.vava.app.model.SportCategory;

public interface EventService {
	/**
	 * vyhlada eventy nachadzajuce sa v v radiuse od zadanej pozicie
	 * @param kilometerRadius rozsah v ktorom prebieha hladanie
	 * @param location bod z ktoreho sa zacne vyhladavanie
	 * @return list eventov
	 */
	public List<Event> getEventsFromRange(int kilometerRadius, Location location);
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
	 * @return true ak sa podarilo odstranit event inak false
	 */
	public boolean removeEvent(int eventId);
	
	/**
	 * Zmena vlastnosti eventu
	 * @param updatedEvent objekt eventu so zmenenymi udajmi
	 */
	public void updateEventDetails(Event updatedEvent);
	
	/**
	 * Vratenie listu vsetkych sporotovych kategorii
	 * @return List kategorii
	 */
	public List<SportCategory> getCategories();
	
	/**
	 * Najde uzivatelov, ktori su prihlaseny na event 
	 * @param eventId id eventu
	 * @return Zoznam id uzivatelov prihlasenych na event
	 */
	public List<Integer> getJoinedUsersOnEvent(int eventId);
}

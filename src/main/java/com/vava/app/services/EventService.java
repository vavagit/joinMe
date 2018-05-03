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
	 * @return true ak sa podarilo zmenit udaje inak false
	 */
	public boolean updateEventDetails(Event updatedEvent);
}

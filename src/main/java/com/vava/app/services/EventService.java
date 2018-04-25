package com.vava.app.services;

import java.util.List;

import com.vava.app.model.Event;

public interface EventService {
	public List<Event> getAllEvents();
	public List<Event> getEventsFromRange(int kilometerRadius);
	public List<Event> getUsersEvent(int userId);
	public Event getEventDetails(int eventId);
	public boolean createEvent(Event newEvent);
	public void removeEvent(int eventId);
	public boolean updateEventDetails(int eventId, Event updatedEvent);
}

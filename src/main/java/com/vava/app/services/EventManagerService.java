package com.vava.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vava.app.model.Event;
import com.vava.app.model.Location;
import com.vava.app.model.database.DatabaseManager;

@Component("eventManager")
public class EventManagerService implements EventService{

	@Autowired
	private DatabaseManager db;

	@Override
	public Event getEventDetails(int eventId) {
		return null;
	}

	@Override
	public boolean createEvent(Event newEvent) {
		return db.createEvent(newEvent);	
	}

	@Override
	public boolean removeEvent(int eventId) {
		return db.removeEvent(eventId);
	}

	@Override
	public List<Event> getEventsFromRange(int kilometerRadius, Location location) {
		return null;
	}

	@Override
	public boolean updateEventDetails(Event updatedEvent) {
		// TODO Auto-generated method stub
		return false;
	}

}

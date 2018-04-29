package com.vava.app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vava.app.model.Event;
import com.vava.app.model.database.DatabaseManager;

@Component("eventManager")
public class EventManagerService implements EventService{

	@Autowired
	private DatabaseManager manager;
	
	@Override
	public List<Event> getAllEvents() {
		return null;
	}

	@Override
	public List<Event> getEventsFromRange(int kilometerRadius) {
		
		return null;
	}
	
	@Override
	public List<Event> getUsersEvent(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Event getEventDetails(int eventId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createEvent(Event newEvent) {
		return true;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEvent(int eventId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean updateEventDetails(int eventId, Event updatedEvent) {
		return true;
	}

}

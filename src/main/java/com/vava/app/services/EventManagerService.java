package com.vava.app.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vava.app.model.Event;
import com.vava.app.model.Location;
import com.vava.app.model.SportCategory;
import com.vava.app.model.User;
import com.vava.app.model.database.DatabaseManager;

@Component("eventManager")
public class EventManagerService implements EventService{

	@Autowired
	private DatabaseManager db;
	@Autowired
	private PropertyManager propManager;
	
    private Logger logger = LogManager.getLogger(EventManagerService.class);
	
	@Override
	public Event getEventDetails(int eventId) {
		return db.getEventDetails(eventId);
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
		//vyberanie prvkov z databazy po 1000
		//skontroluje sa ci je event v hladanom rozsahu
		//vyberanie sa ukonci ak databaza vrati 0 prvkov - boli prezrete vsetky udaje,
		//alebo ak je presiahnuty maximalny pocet eventov na vratenie
		int increase = 1000;
		int maxEventsToReturn = 1000;
		
		try {
			logger.debug("getEventsFromRange, Nahravam hodnoty zo suboru vlastnosti");
			increase = Integer.parseInt(propManager.getProperty("increase"));
			maxEventsToReturn = Integer.parseInt(propManager.getProperty("maxEventsToReturn"));
		} catch(NumberFormatException e) {
			logger.catching(Level.WARN, e);
			e.printStackTrace();
		}
		logger.debug("getEventsFromRange, Hodnoty nacitane increase: " + increase + " maxEventsToReturn: " + maxEventsToReturn);
		
		int offset = 0;
		List<Event> selectedEvents = new ArrayList<>();
		while(true) {
			List<Event> events = db.getEvents(increase, offset);
			logger.debug("getEventsFromRange, Eventy ziskane offset: " + offset);
			if(events.size() == 0)
				return selectedEvents;
			
			logger.debug("getEventsFromRange, Kontorola vzdialenosti eventu");
			//kontrola vzdialenosti eventu od zadaneho miesta
			for(Event event : events) {
				int distance = Location.calculateDistanceInKilometers(event.getEventLocation(), location);
				//ak je event v radiuse pridaj ho k hladanym
				if(distance <= kilometerRadius)
					selectedEvents.add(event);
				//ak bol zoznam naplneny
				if(maxEventsToReturn == selectedEvents.size())
					return selectedEvents;
			}
			offset += increase;
		}
	}

	@Override
	public void updateEventDetails(Event updatedEvent) {
		db.updateEventDetails(updatedEvent);
	}

	@Override
	public List<SportCategory> getCategories() {
		return db.getCategories();
	}

	@Override
	public List<User> getJoinedUsersOnEvent(int eventId) {
		List<User> users = db.getJoinedUsersOnEvent(eventId);
		for(User user : users) {
			user.setPassword("");
		}
		return users;
	}

}

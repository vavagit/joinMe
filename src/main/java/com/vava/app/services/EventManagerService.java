package com.vava.app.services;

import java.util.ArrayList;
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
		
		int offset = 0;
		List<Event> selectedEvents = new ArrayList<>();
		while(true) {
			List<Event> events = db.getEvents(increase, offset);
			
			if(events.size() == 0)
				return selectedEvents;
			
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

}

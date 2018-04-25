package com.vava.app.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.vava.app.model.Event;
import com.vava.app.services.EventManagerService;

@RestController
public class EventController {
	@Autowired
	@Qualifier("eventManager")
	private EventManagerService service;
	
	@GetMapping("/test")
	public Event getTest() {
		return new Event();
	}
	
	@GetMapping("/events")
	public List<Event> getAllEvents(@RequestHeader HttpHeaders header) {
		List<String> value = header.get("Authorization");
		for(String a : value)
			System.out.println(new String(Base64.decodeBase64(a)));
		List<Event> events = new ArrayList<>();
		events.add(new Event(0, 0, "a", null, null, null));
		return events;
		//return service.getAllEvents();
	}
	
	@GetMapping("/events/range/{kilometers}")
	public List<Event> rangedEvents(@PathVariable int kilometers) {
		return service.getEventsFromRange(kilometers);
	}
	
	@GetMapping("/users/{userId}/events")
	public List<Event> getUsersEvents(@PathVariable int userId) {
		return service.getUsersEvent(userId);
	}
	
	@PostMapping("/events/")
	public ResponseEntity<Void> createEvent(@RequestBody Event newEvent) {
		// ak sa podarilo pridat novy event
		if( service.createEvent(newEvent)) {
			URI UriLocation = ServletUriComponentsBuilder.fromCurrentRequest().path(
					"/{id}").buildAndExpand(newEvent.getId()).toUri();
			return ResponseEntity.created(UriLocation).build();	
		}
		// nepodarilo sa vytvorit novy event
		else {
			return ResponseEntity.noContent().build();
		}
	}
	
	@DeleteMapping("/events/")
	public ResponseEntity<Void> removeEvent(int eventId) {
		return null;
		
	}
	
	/**
	 * Zmena udajov o {@link Event}.
	 * @param eventId - sluzi na identifikaciu eventu pomocou jeho ID
	 * @param updatedEvent - objekt obsahujuci updateovane udaje o aktualnom evente
	 * @return
	 */
	@PutMapping("/events/{eventId}")
	public ResponseEntity<Void> updateEventDetails(@PathVariable int eventId, @RequestBody Event updatedEvent) {
		//ak sa podaril update prvku
		if(service.updateEventDetails(eventId, updatedEvent)) {
			
		}
		else {
			
		}
		return null;
	}
	
}

package com.vava.app.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import com.vava.app.model.communication.AccountsManager;
import com.vava.app.services.EventManagerService;

@RestController
public class EventController {
	@Autowired
	@Qualifier("eventManager")
	private EventManagerService service;
	
	@Autowired
	private AccountsManager accountsManager;
	
	@GetMapping("/test")
	public Event getTest() {
		return new Event();
	}
	
	@GetMapping("/events")
	public ResponseEntity<List<Event>> getAllEvents(@RequestHeader HttpHeaders header) {
		
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(accountsManager.authorization(authorizationList)) {
			System.out.println("Prihlasene");
		}
		
		List<Event> events = new ArrayList<>();
		events.add(new Event(0, 0, "a", null, null, null));
		return new ResponseEntity<>(events, HttpStatus.OK);
	}
	
	@GetMapping("/events/range/{kilometers}")
	public ResponseEntity<List<Event>> rangedEvents(@PathVariable int kilometers) {
		return new ResponseEntity<>(service.getEventsFromRange(kilometers),HttpStatus.OK);
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

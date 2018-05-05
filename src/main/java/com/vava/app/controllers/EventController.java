package com.vava.app.controllers;

import java.sql.Date;
import java.time.LocalDate;
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

import com.vava.app.model.Event;
import com.vava.app.model.Location;
import com.vava.app.model.User;
import com.vava.app.services.AccountsManagerService;
import com.vava.app.services.EventManagerService;

@RestController
public class EventController {
	@Autowired
	@Qualifier("eventManager")
	private EventManagerService service;
	
	@Autowired
	private AccountsManagerService accountsManager;
	
	@GetMapping("/test")
	public Event getTest() {
		User user = new User("erik", "password", Date.valueOf(LocalDate.now()), "erik", "maruskin", 
							(float) 5.0, "0911215140", "Beniakova 5A", new Location(10.0, 10.0));
		System.out.println(accountsManager.createUser(user));
		return null;
	}
	
	@GetMapping("/events/{radius}")
	public ResponseEntity<List<Event>> getAllEvents(@PathVariable int radius, @RequestBody Location location, @RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!accountsManager.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		List<Event> events = service.getEventsFromRange(radius, location);
		return new ResponseEntity<>(events, HttpStatus.OK);
	}
	
	@GetMapping("/event/{eventId}")
	public ResponseEntity<Event> getEventDetails(@PathVariable int eventId, @RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!accountsManager.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<>(service.getEventDetails(eventId), HttpStatus.OK);
	}
	
	@PostMapping("/events")
	public ResponseEntity<Void> createEvent(@RequestBody Event newEvent, @RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!accountsManager.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		// ak sa podarilo pridat novy event
		if( service.createEvent(newEvent)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		// nepodarilo sa vytvorit novy event
		else {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
	}
	
	@DeleteMapping("/events")
	public ResponseEntity<Void> removeEvent(@RequestBody int eventId, @RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!accountsManager.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		if(service.removeEvent(eventId))
			return new ResponseEntity<>(HttpStatus.OK);
		else {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
		
	}
	
	/**
	 * Zmena udajov o {@link Event}.
	 * @param eventId - sluzi na identifikaciu eventu pomocou jeho ID
	 * @param updatedEvent - objekt obsahujuci updateovane udaje o aktualnom evente
	 * @return
	 */
	@PutMapping("/events")
	public ResponseEntity<Void> updateEventDetails(@PathVariable int eventId, @RequestBody Event updatedEvent, @RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!accountsManager.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		service.updateEventDetails(updatedEvent);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}

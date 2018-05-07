package com.vava.app.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vava.app.model.Event;
import com.vava.app.model.Location;
import com.vava.app.model.SportCategory;
import com.vava.app.services.AccountsManagerService;
import com.vava.app.services.EventManagerService;

@RestController
public class EventController {
	@Autowired
	@Qualifier("eventManager")
	private EventManagerService service;

    private Logger logger = LogManager.getLogger(EventController.class);
    
	@Autowired
	private AccountsManagerService accountsManager;

	@GetMapping("/test")
	public Event getTest() {
		logger.trace("Trace Message!");
		logger.debug("Debug Message!");
		logger.info("Info Message!");
		logger.warn("Warn Message!");
		logger.error("Error Message!");
		logger.fatal("Fatal Message!");
		return null;
	}


	@GetMapping(value = "/events", params = {"lat" , "lon", "radius"})
	public ResponseEntity<List<Event>> getAllEvents(@RequestParam int radius, @RequestParam double lat, @RequestParam double lon,
			@RequestHeader HttpHeaders header) {
		logger.debug("getAllEvents, request");
		List<String> authorizationList = header.get("Authorization");
		// overenie uzivatela
		if (!accountsManager.authorization(authorizationList)) {
			logger.info("getAllEvents, Autorizacia neuspesna, ziadane: poloha: (" + lat + "," + lon + ") radius: " + radius);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Location location = new Location(lat, lon);
		logger.info("getAllEvents, ziadane: poloha: " + location + " radius: " + radius);
		List<Event> events = service.getEventsFromRange(radius, location);
		return new ResponseEntity<>(events, HttpStatus.OK);
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<Event> getEventDetails(@PathVariable int eventId, @RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		// overenie uzivatela
		if (!accountsManager.authorization(authorizationList)) {
			logger.info("getEventDetails, Autorizacia neuspesna, ziadane: " + eventId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		logger.info("getEventDetails, ziadane: " + eventId);
		return new ResponseEntity<>(service.getEventDetails(eventId), HttpStatus.OK);
	}

	@PostMapping("/events")
	public ResponseEntity<Void> createEvent(@RequestBody Event newEvent, @RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		// overenie uzivatela
		if (!accountsManager.authorization(authorizationList)) {
			logger.info("createEvent, Autorizacia neuspesna");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		// ak sa podarilo pridat novy event
		if (service.createEvent(newEvent)) {
			logger.info("createEvent, vytvorenie noveho eventu uspesne");
			return new ResponseEntity<>(HttpStatus.OK);
		}
		// nepodarilo sa vytvorit novy event
		else {
			logger.info("createEvent, vytvorenie noveho eventu neuspesne");
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
	}

	@DeleteMapping("/events")
	public ResponseEntity<Void> removeEvent(@RequestBody int eventId, @RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		// overenie uzivatela
		if (!accountsManager.authorization(authorizationList)) {
			logger.info("removeEvent, Autorizacia neuspesna, ziadane: " + eventId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		if (service.removeEvent(eventId)) {
			logger.info("removeEvent, odstranenie eventu " + eventId + " uspesne");
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			logger.info("removeEvent, odstranenie eventu " + eventId + " neuspesne");
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}

	}

	/**
	 * Zmena udajov o {@link Event}.
	 * 
	 * @param eventId
	 *            - sluzi na identifikaciu eventu pomocou jeho ID
	 * @param updatedEvent
	 *            - objekt obsahujuci updateovane udaje o aktualnom evente
	 * @return
	 */
	@PutMapping("/events")
	public ResponseEntity<Void> updateEventDetails(@PathVariable int eventId, @RequestBody Event updatedEvent,
			@RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		// overenie uzivatela
		if (!accountsManager.authorization(authorizationList)) {
			logger.info("updateEventDetails, Autorizacia neuspesna, ziadane: " + eventId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		service.updateEventDetails(updatedEvent);
		logger.info("updateEventDetails, zmena udajov: eventId " + eventId + " dokoncena");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/events/categories")
	public ResponseEntity<List<SportCategory>> getCategories(@RequestHeader HttpHeaders header) {
		List<String> authorizationList = header.get("Authorization");
		// overenie uzivatela
		if (!accountsManager.authorization(authorizationList)) {
			logger.info("getCategories, Autorizacia neuspesna");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		logger.info("getCategories, Vraciam kategorie");
		return new ResponseEntity<List<SportCategory>>(service.getCategories(), HttpStatus.OK);
	}
}

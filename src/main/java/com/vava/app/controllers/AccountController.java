package com.vava.app.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.vava.app.model.Event;
import com.vava.app.model.User;
import com.vava.app.model.UserStatus;
import com.vava.app.services.AccountsManagerService;

@RestController
public class AccountController {
	@Autowired
	private AccountsManagerService service;
	
    private Logger logger = LogManager.getLogger(AccountController.class);

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		//ak uzivatel existuje a podarilo sa prihlasit
		User logedUser = service.login(user.getUserName(), user.getPassword());
		if(logedUser != null) {
			logger.info("Prihlasenie uzivatela: " + logedUser.getId() + " uspesne");
			return new ResponseEntity<>(logedUser, HttpStatus.OK);
		}
		logger.info("Prihlasenie uzivatela: " + user.getUserName() + " neuspesne");
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user) {
		//podarilo sa vytvoril uzivatela
		User createdUser = service.createUser(user);
		if(createdUser != null){
			logger.info("Registracia uzivatela: " + createdUser.getId() + " uspesna");
			return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
		}
		logger.info("Registracia uzivatela: " + user.getName() + " neuspesna");
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@GetMapping("/users/{userId}")
	public ResponseEntity<User> getUserDetails(@PathVariable int userId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			logger.info("UserDetails, Autorizacia neuspesna, ziadane: " + userId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		logger.info("UserDetails, ziadane: " + userId);
		return new ResponseEntity<User>(service.getUserDetails(userId), HttpStatus.OK);		
	}
	
	@DeleteMapping("/users/")
	public ResponseEntity<Void> removeUser(@RequestBody User user, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			logger.info("removeUser, Autorizacia neuspesna, ziadane: " + user.getId());
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		if(service.removeUser(user)) {
			logger.info("removeUser, vymazanie uzivatela " + user.getId() + " uspesne");
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			logger.info("removeUser, vymazanie uzivatela " + user.getId() + " neuspesne");
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@GetMapping("/users/{userId}/events")
	public ResponseEntity<List<Event>> getUsersEvents(@PathVariable int userId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			logger.info("getUsersEvents, Autorizacia neuspesna, ziadane: " + userId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		logger.info("getUsersEvents, ziadane " + userId);
		return new ResponseEntity<List<Event>>(service.getUsersEvents(userId), HttpStatus.OK);
	}
	
	@GetMapping("/users/{userId}/created")
	public ResponseEntity<List<Event>> getEventsCreatedByUser(@PathVariable int userId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			logger.info("getEventsCreatedByUser, Autorizacia neuspesna, ziadane: " + userId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		logger.info("getEventsCreatedByUser, ziadane: ", userId);
		return new ResponseEntity<List<Event>>(service.getEventsCreatedByUser(userId), HttpStatus.OK);
	}
	
	@PostMapping("/users/{userId}/event/{eventId}")
	public ResponseEntity<Void> applyToEvent(@PathVariable int userId, @PathVariable int eventId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			logger.info("applyToEvent, Autorizacia neuspesna, ziadane: userId:" + userId + " eventId:" + eventId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(service.applyToEvent(userId, eventId)) {
			logger.info("applyToEvent, ziadane: userId: " + userId + " eventId:" + eventId + " uspesne");
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			logger.info("applyToEvent, ziadane: userId: " + userId + " eventId:" + eventId + " neuspesne");
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@DeleteMapping("/users/{userId}/event/{eventId}")
	public ResponseEntity<Void> removeApplicationToEvent(@PathVariable int userId, @PathVariable int eventId, 
			@RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			logger.info("removeApplicationToEvent, Autorizacia neuspesna, ziadane: userId:" + userId + " eventId:" + eventId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(service.removeApplicationToEvent(userId, eventId)) {
			logger.info("removeApplicationToEvent, ziadane: userId: " + userId + " eventId:" + eventId + " uspesne");
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			logger.info("removeApplicationToEvent, ziadane: userId: " + userId + " eventId:" + eventId + " neuspesne");
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@GetMapping("/users/{eventId}/event/{userId}")
	public ResponseEntity<UserStatus> getUsersStatusToEvent(@PathVariable int eventId, @PathVariable int userId, 
			@RequestHeader HttpHeaders header) {
		logger.info("getUsersStatusToEvent, Poziadavka");
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			logger.info("getUsersStatusToEvent, Autorizacia neuspesna, ziadane: userId:" + userId + " eventId:" + eventId);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		UserStatus status = service.getUserStatusToEvent(userId, eventId);
		logger.info("getUsersStatusToEvent, Spracovana EventId: " + eventId + " userId:" + userId + " status:" + status);
		return new ResponseEntity<> (status, HttpStatus.OK);
	}
}

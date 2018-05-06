package com.vava.app.controllers;

import java.util.List;

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
import com.vava.app.services.AccountsManagerService;

@RestController
public class AccountController {
	@Autowired
	private AccountsManagerService service;
	
	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		System.out.println("Prichadzajuca sprava" + user);
		//ak uzivatel existuje a podarilo sa prihlasit
		User logedUser = service.login(user.getUserName(), user.getPassword());
		if(logedUser != null)
			return new ResponseEntity<>(logedUser, HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user) {
		//podarilo sa vytvoril uzivatela
		User createdUser = service.createUser(user);
		if(createdUser != null){
			System.out.println("Uzivatel zaregistrovany: ");
			System.out.println(createdUser);
			return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
		}
		System.out.println("registracia sa nepodarila");
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@GetMapping("/users/{userId}")
	public ResponseEntity<User> getUserDetails(@PathVariable int userId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<User>(service.getUserDetails(userId), HttpStatus.OK);		
	}
	
	@DeleteMapping("/users/")
	public ResponseEntity<Void> removeUser(@RequestBody User user, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		if(service.removeUser(user))
			return new ResponseEntity<>(HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@GetMapping("/users/{userId}/events")
	public ResponseEntity<List<Event>> getUsersEvents(@PathVariable int userId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<List<Event>>(service.getUsersEvents(userId), HttpStatus.OK);
	}
	
	@GetMapping("/users/{userId}/created")
	public ResponseEntity<List<Event>> getEventsCreatedByUser(@PathVariable int userId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<List<Event>>(service.getEventsCreatedByUser(userId), HttpStatus.OK);
	}
	
	@PostMapping("/users/{userId}/event/{eventId}")
	public ResponseEntity<Void> applyToEvent(@PathVariable int userId, @PathVariable int eventId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(service.applyToEvent(userId, eventId))
			return new ResponseEntity<>(HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@DeleteMapping("/users/{userId}/event/{eventId}")
	public ResponseEntity<Void> removeApplicationToEvent(@PathVariable int userId, @PathVariable int eventId, @RequestHeader HttpHeaders header){
		List<String> authorizationList = header.get("Authorization");
		//overenie uzivatela
		if(!service.authorization(authorizationList)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(service.removeApplicationToEvent(userId, eventId))
			return new ResponseEntity<>(HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	
}

package com.vava.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.vava.app.model.User;
import com.vava.app.services.AccountsManagerService;

@RestController
public class AccountController {
	@Autowired
	private AccountsManagerService service;
	
	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestBody User user) {
		//ak uzivatel existuje a podarilo sa prihlasit
		if(service.login(user.getUserName(), user.getPassword()))
			return new ResponseEntity<>(HttpStatus.OK);
		return new ResponseEntity<Void>(HttpStatus.CONFLICT);
	}
	
	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody User user) {
		//podarilo sa vytvoril uzivatela
		if(service.createUser(user)) {
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
}

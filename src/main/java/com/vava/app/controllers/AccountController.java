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
	public ResponseEntity<User> login(@RequestBody User user) {
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
			return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
}

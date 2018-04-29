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
	public ResponseEntity<Boolean> login(@RequestBody User user) {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<Boolean> register(@RequestBody User user) {
		return new ResponseEntity<Boolean>(service.createUser(user), HttpStatus.OK);
	}
}

package com.services;

import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.vava.app.model.Location;
import com.vava.app.model.User;
import com.vava.app.services.AccountsManagerService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = com.vava.app.JoinMeApplication.class)
@SpringBootTest
public class AccountManagerTests {

	@Autowired
	private AccountsManagerService service;
	
	@Test
	public void createAndRemoveUserTest() {
		User user = new User("erik", "password", Date.valueOf(LocalDate.now()), "erik", "maruskin", 
				(float) 5.0, "0911215140", "Beniakova 5A", new Location(10.0, 10.0));
		assertTrue(service.createUser(user) != null);
		assertTrue(service.createUser(user) == null);
		assertTrue(service.removeUser(user));
		assertTrue(!service.removeUser(user));
	}

	@Test
	public void createRemoveLoginUserTest() {
		User user = new User("erik", "password", Date.valueOf(LocalDate.now()), "erik", "maruskin", 
				(float) 5.0, "0911215140", "Beniakova 5A", new Location(10.0, 10.0));
		assertTrue(service.createUser(user) != null);
		assertTrue(service.login("erik", "password") != null);
		assertTrue(service.login("erik", "passwod") == null);
		assertTrue(service.login("erika", "password") == null);
		assertTrue(service.removeUser(user));
		assertTrue(service.login("erik", "password") == null);
	}
}

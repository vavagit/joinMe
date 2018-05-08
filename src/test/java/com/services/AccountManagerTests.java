package com.services;

import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.vava.app.model.Event;
import com.vava.app.model.Location;
import com.vava.app.model.User;
import com.vava.app.model.UserStatus;
import com.vava.app.services.AccountsManagerService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = com.vava.app.JoinMeApplication.class)
@SpringBootTest
public class AccountManagerTests {

	@Autowired
	private AccountsManagerService service;
	
	//@Test
	public void createAndRemoveUserTest() {
		User user = new User("erik", "password", Date.valueOf(LocalDate.now()), "erik", "maruskin", 
				(float) 5.0, "0911215140", "Beniakova 5A", new Location(10.0, 10.0));
		User crated = service.createUser(user);
		assertTrue(crated != null);
		System.out.println(crated);
		assertTrue(service.createUser(user) == null);
		assertTrue(service.removeUser(user));
		assertTrue(!service.removeUser(user));
	}

	//@Test
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
	
	//@Test
	public void usersEventsTest() {
		List<Event> events = service.getUsersEvents(8);
		assertTrue(events.size() == 1);
	}

	//@Test
	public void getEventsCreatedByUserTest() {
		List<Event> events = service.getEventsCreatedByUser(789);
		assertTrue(events.size() == 0);
	}
	
	//@Test
	public void userDetailsTest() {
		User user = service.getUserDetails(5);
		System.out.println(user);
		assertTrue(user != null);
		user = service.getUserDetails(456);
		assertTrue(user == null);
	}
	
	//@Test
	public void applyToEventTest() {
		int userId = 5;
		int eventId = 7;
		assertTrue(service.applyToEvent(userId, eventId));
		assertTrue(!service.applyToEvent(userId, eventId));
		assertTrue(service.removeApplicationToEvent(userId, eventId));
		assertTrue(!service.removeApplicationToEvent(userId, eventId));
	}
	
	@Test
	public void getStatusTest() {
		UserStatus status = service.getUserStatusToEvent(9, 61);
		assertTrue(status.equals(UserStatus.JOINED));
		status = service.getUserStatusToEvent(10, 61);
		assertTrue(status.equals(UserStatus.NONE));
		status = service.getUserStatusToEvent(50, 9);
		assertTrue(status.equals(UserStatus.CREATOR));
	}
}

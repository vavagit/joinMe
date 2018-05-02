package com.services;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.vava.app.model.Event;
import com.vava.app.services.EventManagerService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = com.vava.app.JoinMeApplication.class)
@SpringBootTest
public class EventsManagerTests {
	@Autowired
	private EventManagerService service;
	
	@Test
	public void usersEventsTest() {
		List<Event> events = service.getUsersEvents(8);
//		for(Event current: events)
//			System.out.println(current);
		assertTrue(events.size() == 1);
	}

	@Test
	public void getEventsCreatedByUser() {
		List<Event> events = service.getEventsCreatedByUser(789);
		for(Event current: events)
			System.out.println(current);
		assertTrue(events.size() == 0);
	}
}

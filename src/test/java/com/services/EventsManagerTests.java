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
import com.vava.app.model.Location;
import com.vava.app.model.SportCategory;
import com.vava.app.services.EventManagerService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = com.vava.app.JoinMeApplication.class)
@SpringBootTest
public class EventsManagerTests {
	@Autowired
	private EventManagerService service;
	
	@Test
	public void createAndRemoveEventTest() {
		//Event event = new Event(0, 45, "beh", "bude sa behat", new Date(2018, 9, 14), 15, 5, new SportCategory(1, "behanie"), "Beniakova 5a", new Location(10.0, 10.0));
		//assertTrue(service.createEvent(event));
		//assertTrue(service.removeEvent(105));
	}

	@Test(timeout = 1000)
	public void getEventsTest() {
		List<Event> events = service.getEventsFromRange(150, new Location(10,10));
		assertTrue(events.size() > 0);
	}
	
	@Test
	public void testCategories() {
		List<SportCategory> categories = service.getCategories();
		for(SportCategory a : categories) {
			System.out.println(a);
		}
	}
	
}

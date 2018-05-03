package com.vava.app.model.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;

import com.vava.app.model.Event;

public class EventStatementSetter implements PreparedStatementSetter {
	private Event event;
	
	public EventStatementSetter(Event event) {
		this.event = event;
	}
	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		ps.setString(1, event.getEventName());
		ps.setString(2, event.getDescription());
		ps.setInt(3, event.getMaxUsersOnEvent());
		ps.setInt(4, event.getCreatorId());
		ps.setInt(5, event.getSportCategory().getId());
		ps.setDouble(6, event.getEventLocation().getLatitude());
		ps.setDouble(7, event.getEventLocation().getLongitude());
		ps.setDate(8, event.getDate());
		ps.setInt(9, event.getNecessaryAge());
		ps.setString(10, event.getAddress());
	}
}

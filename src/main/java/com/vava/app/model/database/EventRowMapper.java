package com.vava.app.model.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.vava.app.model.Event;
import com.vava.app.model.Location;
import com.vava.app.model.SportCategory;

public class EventRowMapper implements RowMapper<Event> {

	@Override
	public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
		int eventId = rs.getInt("id");
		SportCategory category = new SportCategory(rs.getInt("category_id"), rs.getString("sport"));
		Event event = new Event(eventId,rs.getInt("max_users"), rs.getString("name"),
								rs.getString("description"), rs.getDate("event_date"), 
								rs.getInt("necessary_age"), rs.getInt("user_id_creator"), category, 
								rs.getString("address"),
								new Location(rs.getDouble("latitude"), rs.getDouble("longitude")));
		return event;
	}

}

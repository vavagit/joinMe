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
		SportCategory category = new SportCategory(rs.getInt("sport_category_id"), rs.getString("sport_sk"), rs.getString("sport_en"));
		return new Event(rs.getInt("id"),rs.getInt("max_users"), rs.getString("name"),
								rs.getString("description"), rs.getDate("event_date"), 
								rs.getInt("necessary_age"), rs.getInt("user_id_creator"), category, 
								rs.getString("address"),
								new Location(rs.getDouble("latitude"), rs.getDouble("longitude")));
	}

}

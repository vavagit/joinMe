package com.vava.app.model.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.vava.app.model.Location;
import com.vava.app.model.User;

public class UserRowMapper implements RowMapper<User>{

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User(rs.getString("user_name"), rs.getString("password"),
							rs.getDate("registrated_at"), rs.getString("name"), 
							rs.getString("last_name"), rs.getFloat("rating"), 
							rs.getString("contact"), rs.getString("address"),
							new Location(rs.getDouble("latitude"), rs.getDouble("longitude")));
		user.setId(rs.getInt("id"));
		return user;
	}

}

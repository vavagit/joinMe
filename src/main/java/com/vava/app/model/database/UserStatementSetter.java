package com.vava.app.model.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;

import com.vava.app.model.User;

public class UserStatementSetter implements PreparedStatementSetter {
	private User user;
	
	public UserStatementSetter(User user) {
		this.user = user;
	}
	
	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		ps.setString(1, user.getUserName());
		ps.setString(2, user.getPassword());
		ps.setDate(3, user.getRegisteredAt());
		ps.setString(4, user.getName());
		ps.setString(5, user.getLastName());
		ps.setDouble(6, user.getRating());
		ps.setString(7, user.getContact());
		ps.setString(8, user.getAddress());
		ps.setString(9, user.getAddressLocation().getLongitude() + "");
		ps.setString(10, user.getAddressLocation().getLatitude() + "");
	}
}

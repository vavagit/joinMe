package com.vava.app.services;

import com.vava.app.model.User;

public interface AccountService {
	public boolean createUser(User user);
	public boolean login(String username, String password);
	public User findUserByEmail(String email);
}

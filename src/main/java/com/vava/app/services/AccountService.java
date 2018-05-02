package com.vava.app.services;

import com.vava.app.model.User;

public interface AccountService {
	public User createUser(User user);
	public User login(String username, String password);
	public User findUserByUserName(String name);
	public User getUserDetails(int id);
	public boolean removeUser(User user);
}

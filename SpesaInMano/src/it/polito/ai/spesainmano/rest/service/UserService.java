package it.polito.ai.spesainmano.rest.service;

import java.sql.SQLException;

import it.polito.ai.spesainmano.model.User;

public interface UserService {

	User create(User u) throws SQLException;
	
	int getPoints(int id) throws SQLException;

	boolean checkEmail(User u) throws SQLException;
}

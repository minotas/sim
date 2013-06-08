package it.polito.ai.spesainmano.rest.service;

import java.sql.SQLException;

import it.polito.ai.spesainmano.model.User;

public interface LoginService {
	
	User login(User u) throws SQLException;
}

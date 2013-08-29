package it.polito.ai.spesainmano.rest.service;

import java.sql.SQLException;

import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;

public interface UserService {

	void validateRegisterForm(User user) throws CustomBadRequestException;
	
	User create(User u) throws CustomBadRequestException, CustomServiceUnavailableException;
	
	User getUserInfo(int id) throws CustomNotFoundException, CustomServiceUnavailableException ;
	
	//int getPoints(int id) throws SQLException;

}

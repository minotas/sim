package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;

public interface LoginService {
	
	void validateForm(User u) throws  CustomBadRequestException ; 
	
	User login(User u) throws CustomNotFoundException, CustomServiceUnavailableException;
}

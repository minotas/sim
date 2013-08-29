package it.polito.ai.spesainmano.rest.resource;


import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.LoginService;
import it.polito.ai.spesainmano.rest.serviceimpl.LoginServiceImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Receives the requests to make the users login
 * @version 1.0
 */
@Path("/login")
public class LoginResource {
      
private  LoginService loginService;
      

/** 
 * Manages the post requests refers to the user login 
 * @param user An user object containing the user information to do the login
 * @return an user object containing the information when the login success
 * @throws CustomBadRequestException Generated when the email or password fields are empty
 * @throws CustomNotFoundException Generated when the user is not registered or the password is wrong
 * @throws CustomServiceUnavailable Generated when the service is not available
 */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public User login(User user) throws CustomBadRequestException, CustomNotFoundException, CustomServiceUnavailableException{
    	
    	loginService = new LoginServiceImpl();
		loginService.validateForm(user);
    	return loginService.login(user);
	
    }
    
}

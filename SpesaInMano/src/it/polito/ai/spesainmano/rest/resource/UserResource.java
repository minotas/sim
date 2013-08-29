package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;

import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.UserService;
import it.polito.ai.spesainmano.rest.serviceimpl.UserServiceImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Receives the requests to make the related to the users
 * 
 * @version 1.0
 */
@Path("/user")
public class UserResource {

	private UserService userService;

	/**
	 * Manages the post requests refers to the user registration
	 * 
	 * @param user An user object containing the user information to do the registration
	 * @return an user object containing the information of the user, including the id assigned.
	 * @throws CustomBadRequestException Generated when one or more fields of the user are invalid or the user is 
	 * 		   already registered
	 * @throws CustomServiceUnavailable Generated when the service is not available
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public User create(User user) throws CustomBadRequestException, CustomServiceUnavailableException{

		userService = new UserServiceImpl();
		userService.validateRegisterForm(user);
		return userService.create(user);

	}

	/*
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public User getUserInfo(@PathParam("id") int id) {

		userService = new UserServiceImpl();
		return userService.getUserInfo(id);

	}
*/
}

package it.polito.ai.spesainmano.rest.resource;

import java.sql.SQLException;

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

@Path("/login")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class LoginResource {
      
private  LoginService loginService;
      
    @POST
    public User login(User user) {
		if(user.getEmail().equals("") || user.getPassword().equals("")){
			throw new CustomBadRequestException("Incomplete username or password");
		}
    	loginService = new LoginServiceImpl();
		User usr;
		try {
			usr = loginService.login(user);

			if (usr == null) {
				throw new CustomNotFoundException("Wrong username or password");
			}
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		return usr;
	}
}

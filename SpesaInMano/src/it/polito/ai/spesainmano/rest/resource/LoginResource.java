package it.polito.ai.spesainmano.rest.resource;


import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.service.LoginService;
import it.polito.ai.spesainmano.rest.serviceimpl.LoginServiceImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/login")
public class LoginResource {
      
private  LoginService loginService;
      
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public User login(User user){
    	
    	loginService = new LoginServiceImpl();
		loginService.validateForm(user);
    	return loginService.login(user);
	
    }
    
}

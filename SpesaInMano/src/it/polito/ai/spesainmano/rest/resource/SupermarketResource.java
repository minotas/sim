package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnathorizedException;
import it.polito.ai.spesainmano.rest.service.SupermarketService;
import it.polito.ai.spesainmano.rest.serviceimpl.SupermarketServiceImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/supermarket")
public class SupermarketResource {

	SupermarketService ss;
	
	@GET
	@Path("/checkin")
	@Produces({MediaType.APPLICATION_JSON})
	public List<Supermarket> checkin(@QueryParam("latitude") float latitude, @QueryParam("longitude") float longitude, @Context HttpHeaders hh) {
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
			  throw new CustomUnathorizedException("The user isn't logged in");
		}
		try {
			ss = new SupermarketServiceImpl();
			List<Supermarket> supermarkets = ss.checkIn(latitude, longitude);
			if(supermarkets.size() == 0){
				throw new CustomNotFoundException("You aren't in a supermarket");
			}
			else return supermarkets;
		
		} catch (SQLException e) {
		 	throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
		}
	}
}
package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.SupermarketService;
import it.polito.ai.spesainmano.rest.serviceimpl.SupermarketServiceImpl;

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

/**
 * Receives the requests related to the supermarkets
 * @version 1.0
 */
@Path("/supermarket")
public class SupermarketResource {

	SupermarketService supermarketService;
	
	/**
	 * Manages the get requests about the check-in in a supermarket - Requires Authentication
	 * 
	 * @param latitude The latitude of the physical position of the user
	 * @param longitude The longitude of the physical position of the user
	 * @return a List of supermarkets located in 100m of radio from the position of the user
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomBadRequestException Generated when the longitude or the latitude are wrong
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when there isn't any near supermarket
	 */
	@GET
	@Path("/checkin")
	@Produces({MediaType.APPLICATION_JSON})
	public List<Supermarket> checkin(@QueryParam("latitude") float latitude, @QueryParam("longitude") float longitude, @Context HttpHeaders hh) {
	
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		supermarketService = new SupermarketServiceImpl();
		return supermarketService.checkIn(latitude, longitude);
	
	}

	
}

package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.responses.NumberOfMonitoredSupermarketsResponse;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.MonitoredSupermarketService;
import it.polito.ai.spesainmano.rest.serviceimpl.MonitoredSupermarketServiceImpl;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * Receives the requests related to the Monitored Supermarkets
 * @version 1.0
 */
@Path("/monitoredSupermarket")
public class MonitoredSupermarketResource {

	private MonitoredSupermarketService monitoredSupermarketService;

	/**
	 * Manages the post requests about adding a set of monitored supermarkets by an user
	 * @param msList A list of MonitoredSupermarket Object to be added containing all the required information
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 */
	@POST
	@Consumes({MediaType.APPLICATION_JSON}) 
	@Produces({ MediaType.APPLICATION_JSON})
	public void create(List<MonitoredSupermarket> msList, @Context HttpHeaders hh) {
		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		int userId = Integer.parseInt(pathParams.get("id_user").getValue());
		monitoredSupermarketService = new MonitoredSupermarketServiceImpl();
		monitoredSupermarketService.insertMonitoredSupermarkets(msList, userId);
		return;

	}
	

	/**
	 * Manages the post requests about deleting a set of monitored supermarkets by an user
	 * @param msList A list of MonitoredSupermarket Object to be deleted containing all the required information
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 */
	@Path("/delete")
	@POST
	@Consumes({MediaType.APPLICATION_JSON}) 
	public void delete(List<MonitoredSupermarket> msList, @Context HttpHeaders hh) {
		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {

			throw new CustomUnauthorizedException("The user isn't logged in");

		}
		int userId = Integer.parseInt(pathParams.get("id_user").getValue());

		monitoredSupermarketService = new MonitoredSupermarketServiceImpl();
		monitoredSupermarketService.deleteMonitoredSupermarkets(msList, userId);
		
		return;

	}

	/**
	 * Manages the get requests about getting the near supermarkets and which of them are monitored
	 * @return A list of MonitoredSupermarkets objects 
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	*/	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<MonitoredSupermarket> getSupermarkets(@QueryParam("latitude") float latitude, @QueryParam("longitude") float longitude, @Context HttpHeaders hh) {

		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		int userId = Integer.parseInt(pathParams.get("id_user").getValue());
		monitoredSupermarketService = new MonitoredSupermarketServiceImpl();
		
		return monitoredSupermarketService.getSupermarkets(latitude, longitude,	userId);
	}
	
	/**
	 * Manages the get requests about getting the number of monitored supermarkets by an user
	 * @return The number of monitored Supermarkets
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	*/	
	@Path("/number")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public NumberOfMonitoredSupermarketsResponse getMonitoredSupermarketsNumber(@Context HttpHeaders hh) {

		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		int userId = Integer.parseInt(pathParams.get("id_user").getValue());
		monitoredSupermarketService = new MonitoredSupermarketServiceImpl();
		
		int number = monitoredSupermarketService.getMonitoredSupermarketsNumber(userId);
		NumberOfMonitoredSupermarketsResponse response = new NumberOfMonitoredSupermarketsResponse();
		response.setNumber(number);
		return response;
	}

}

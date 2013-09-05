package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.MonitoredSupermarket;
import it.polito.ai.spesainmano.responses.Response;
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

@Path("/monitoredSupermarket")
public class MonitoredSupermarketResource {

	private MonitoredSupermarketService monitoredSupermarketService;

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
	
	@Path("/number")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getMonitoredSupermarketsNumber(@Context HttpHeaders hh) {

		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		int userId = Integer.parseInt(pathParams.get("id_user").getValue());
		monitoredSupermarketService = new MonitoredSupermarketServiceImpl();
		
		int number = monitoredSupermarketService.getMonitoredSupermarketsNumber(userId);
		Response response = new Response();
		response.setNumber(number);
		return response;
	}

}

package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.MonitoredProduct;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.MonitoredProductService;
import it.polito.ai.spesainmano.rest.serviceimpl.MonitoredProductServiceImpl;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * Receives the requests related to the Monitored Products
 * @version 1.0
 */
@Path("/monitoredProduct")
public class MonitoredProductResource {

	private MonitoredProductService monitoredProductService;

	/**
	 * Manages the post requests about creating a new monitored product for a user
	 * @param mp A mpObject containing the information required to add the new monitored product
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomBadRequestException Generated when the product is already monitored by the user
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public MonitoredProduct create(MonitoredProduct mp, @Context HttpHeaders hh)throws CustomUnauthorizedException, CustomBadRequestException, CustomServiceUnavailableException{

		Map<String, Cookie> pathParams = hh.getCookies();

		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}

		MonitoredProduct monP = mp;
		User u = new User();
		u.setId_user(Integer.parseInt(pathParams.get("id_user").getValue()));
		monP.setId_user(u); 
		monitoredProductService= new MonitoredProductServiceImpl();
		return monitoredProductService.insert(mp);


	}

	/**
	 * Manages the get requests about getting all the monitored products by an user
	 * @return A list of MonitoredProducts objects 
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomBadRequestException Generated when the product is already monitored by the user
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public List<MonitoredProduct> getMonitoredProducts(@Context HttpHeaders hh) throws CustomServiceUnavailableException, CustomUnauthorizedException, CustomBadRequestException
	{
		Map<String, Cookie> pathParams = hh.getCookies();

		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}

		User u = new User();
		u.setId_user(Integer.parseInt(pathParams.get("id_user").getValue()));
		monitoredProductService= new MonitoredProductServiceImpl();
		return monitoredProductService.getMonitoredProducts(u);

	}

	/**
	 * Manages the post requests about delete a group of monitored products
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 */
	@Path("/delete")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public void deleteMonitoredProducts(List<MonitoredProduct> mpList,  @Context HttpHeaders hh) throws CustomUnauthorizedException, CustomServiceUnavailableException{
		Map<String, Cookie> pathParams = hh.getCookies();

		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}

		User u = new User();
		u.setId_user(Integer.parseInt(pathParams.get("id_user").getValue()));
		monitoredProductService= new MonitoredProductServiceImpl();
		monitoredProductService.deleteMonitoredProducts(mpList, u);
		return;

	}

}

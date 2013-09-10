package it.polito.ai.spesainmano.rest.resource;

import java.util.List;
import java.util.Map;

import it.polito.ai.spesainmano.responses.Statistic;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.StatisticService;
import it.polito.ai.spesainmano.rest.serviceimpl.StatisticServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * Receives the requests related to the Statistics
 * @version 1.0
 */
@Path("/statistic")
public class StatisticResource {
	private StatisticService statisticService;
	
	
	/**
	 * Manages the get requests about getting the statistics of a product in a given supermarket in the last six months
	 * Requires authentication
	 * 
	 * @param supermarketId The id of the Supermarket
	 * @param productId The id of the product
	 * @return a List of Statistic objects containing the average prices of the last six month
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when no statistics are found
	*/
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public List<Statistic> getProductsByProductType(@QueryParam("id_supermarket") int supermarketId, @QueryParam("id_product") int productId, @Context HttpHeaders hh) {
		
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		statisticService = new StatisticServiceImpl();
		return statisticService.getAveragesLastSixMonths(supermarketId, productId);

	}
}

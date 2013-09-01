package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.ListItem;
import it.polito.ai.spesainmano.model.MarketList;
import it.polito.ai.spesainmano.responses.MarketListDetail;
import it.polito.ai.spesainmano.responses.SupermarketListPrice;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.MarketListService;
import it.polito.ai.spesainmano.rest.serviceimpl.MarketListServiceImpl;
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
 * Receives the requests related to the Market List
 * @version 1.0
 */
@Path("/marketList")
public class MarketListResource {

	private MarketListService marketListService;

	/**
	 * Manages the post requests about creating a new market list - Requires user authentication
	 * @param listItems A list of items selected by the user in his market list.
	 * @return A MarketList Object containing the id of the new market list created.
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomBadRequestException Generated when the quantity of one list item is not a positive integer
	*/
	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public MarketList create(List<ListItem> listItems, @Context HttpHeaders hh) throws CustomBadRequestException, CustomServiceUnavailableException, CustomUnauthorizedException{
		
		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}

		int userId = Integer.parseInt(hh.getCookies().get("id_user").getValue());
		marketListService = new MarketListServiceImpl();
		marketListService.validateMarketList(listItems);
		return marketListService.create(listItems, userId);

	}
	
	/**
	 * Manages the get requests about getting the total price of a supermarket list in some supermarkets
	 * Requires user authentication
	 * @return A list of SupermarketListPrice Objects containing the number of products found, the price of the
	 * 		   market list in the most visited and monitored supermarkets.
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomNotFoundException Generated when the user doesn't have a market list
	*/
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public List<SupermarketListPrice> getTotals( @Context HttpHeaders hh) throws CustomNotFoundException, CustomServiceUnavailableException, CustomUnauthorizedException {
		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}

		int idUser = Integer.parseInt(hh.getCookies().get("id_user").getValue());
		marketListService = new MarketListServiceImpl();
		return marketListService.getTotalInSupermarkets(idUser);

	}
	
	/**
	 * Manages the get requests about getting the details(item by item info) of a supermarket list in a specific
	 * supermarket - Requires user authentication
	 * @param supermarketId The id of the supermarket in which will be obtained the details of the market list
	 * @return A list of MarketListDetail Objects containing the info of the products and their prices
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomNotFoundException Generated when the user doesn't have a market list
	*/	
	@Path("/details")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<MarketListDetail> getDetails(@QueryParam("id_supermarket") int supermarketId, @Context HttpHeaders hh) throws CustomNotFoundException, CustomServiceUnavailableException, CustomUnauthorizedException {
		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}

		int idUser = Integer.parseInt(hh.getCookies().get("id_user").getValue());
		marketListService = new MarketListServiceImpl();
		return marketListService.getMarketListDetails(supermarketId, idUser);

	}
	
	
	

}

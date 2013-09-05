package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.responses.InsertPriceResponse;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.PriceService;
import it.polito.ai.spesainmano.rest.serviceimpl.PriceServiceImpl;

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
 * Receives the requests related to the prices
 * @version 1.0
 */
@Path("/price")
public class PriceResource {

	private PriceService priceService;

	/**
	 * Manages the post requests about adding a new price of a product in a supermarket - Requires authentication
	 * 
	 * @param p An Price object containing the information of the new price, including the product and supermarket
	 * @return a InsertPriceResponseObject containing the qualifier of the price inserted and the point of the user
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomBadRequestException Generated when the information received is not valid, is not complete or when the 
	 * 		   price inserted does not follows the reliability politics of Spesa in Mano
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public InsertPriceResponse create(Price p, @Context HttpHeaders hh)	throws CustomBadRequestException {
		
		Map<String, Cookie> pathParams = hh.getCookies();
		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		Price price = p;
		User u = new User();
		u.setId_user(Integer.parseInt(pathParams.get("id_user").getValue()));
		price.setId_user(u);
		priceService = new PriceServiceImpl();
		priceService.validate(p);
		priceService.checkPrice(p);
		return priceService.insert(p);

	}

	/**
	 * Manages the get requests about getting the prices of a product in the near and monitored supermarkets
	 * Requires authentication
	 * 
	 * @param productId The id of the product
	 * @param longitude The longitude of the current position of the user
	 * @param latitude The latitude of the current position of the user
	 * @param supermarketId The id of the supermarket in which is the user
	 * @return A list of prices containing the prices and the supermarkets
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomBadRequestException Generated when the longitude or the latitude are invalid
	 * @throws CustomNotFoundException Generated when there aren't prices of the product in the near or monitored supermarkets
	 */
	@Path("/compare")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Price> getPriceNearSupermarkets(@QueryParam("productId") int productId, @QueryParam("longitude") float longitude, @QueryParam("latitude") float latitude,@QueryParam("supermarketId") int supermarketId,  @Context HttpHeaders hh)
			throws CustomUnauthorizedException, CustomServiceUnavailableException, CustomBadRequestException{

		Map<String, Cookie> pathParams = hh.getCookies();
		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		priceService = new PriceServiceImpl();
		return priceService.getNearSupermarketsPrices(Integer.parseInt(pathParams.get("id_user").getValue()), productId, latitude, longitude, supermarketId);


	}
 

	/**
	 * Manages the get requests about getting the offer prices
	 * @param longitude The longitude of the current position of the user
	 * @param latitude The latitude of the current position of the user
	 * @return A list of prices containing the prices and the supermarkets
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomBadRequestException Generated when the longitude or the latitude are invalid
	 * @throws CustomNotFoundException Generated when there products in offer
	 */
	@Path("/offers")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Price> getOffers(@QueryParam("longitude") float longitude, @QueryParam("latitude") float latitude,  @Context HttpHeaders hh) throws CustomUnauthorizedException, CustomServiceUnavailableException, CustomBadRequestException{

		Map<String, Cookie> pathParams = hh.getCookies();
		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		int idUser = Integer.parseInt(pathParams.get("id_user").getValue());
		PriceService priceService = new PriceServiceImpl();
		return priceService.getOffers(idUser, latitude, longitude);
		
	}

	
	/*
	 * @GET
	 * 
	 * @Produces({ MediaType.APPLICATION_JSON})
	 * 
	 * public float getMaxMinPrices(@QueryParam("productId") int productId,
	 * @QueryParam("SupermarketId") int supermarketId, @Context HttpHeaders hh)
	 * { float averageLastSixMonths; Map<String, Cookie> pathParams =
	 * hh.getCookies(); if(!pathParams.containsKey("id_user")){ throw new
	 * CustomUnathorizedException("The user isn't logged in"); }
	 * 
	 * try { priceService = new PriceServiceImpl(); averageLastSixMonths =
	 * priceService.getAverageLastSixMonths(productId, supermarketId);
	 * 
	 * return averageLastSixMonths;
	 * 
	 * } catch (SQLException e) { throw new CustomServiceUnavailableException(
	 * "Server received an invalid response from upstream server"); }
	 * 
	 * }
	 */
}

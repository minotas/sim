package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.User;
import it.polito.ai.spesainmano.responses.InsertPriceResponse;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomUnathorizedException;
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


@Path("/price")
public class PriceResource {

	private PriceService priceService;

	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public InsertPriceResponse create(Price p, @Context HttpHeaders hh)
			throws CustomBadRequestException {
		
		Map<String, Cookie> pathParams = hh.getCookies();
		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnathorizedException("The user isn't logged in");
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

	@Path("/compare")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Price> getPriceNearSupermarkets(@QueryParam("productId") int productId, @QueryParam("longitude") float longitude, @QueryParam("latitude") float latitude,@QueryParam("supermarketId") int supermarketId,  @Context HttpHeaders hh) {

		Map<String, Cookie> pathParams = hh.getCookies();
		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnathorizedException("The user isn't logged in");
		}

	
		
			priceService = new PriceServiceImpl();
			return priceService.getNearSupermarketsPrices(Integer.parseInt(pathParams.get("id_user").getValue()), productId, latitude, longitude, supermarketId);


	}

	
	@Path("/offers")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Price> getOffers(@QueryParam("longitude") float longitude, @QueryParam("latitude") float latitude,  @Context HttpHeaders hh) {

		Map<String, Cookie> pathParams = hh.getCookies();
		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnathorizedException("The user isn't logged in");
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

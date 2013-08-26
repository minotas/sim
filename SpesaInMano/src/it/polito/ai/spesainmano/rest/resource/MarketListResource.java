package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.ListItem;
import it.polito.ai.spesainmano.model.MarketList;
import it.polito.ai.spesainmano.responses.MarketListDetails;
import it.polito.ai.spesainmano.responses.SupermarketListPrice;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.exception.CustomUnathorizedException;
import it.polito.ai.spesainmano.rest.service.MarketListService;
import it.polito.ai.spesainmano.rest.serviceimpl.MarketListServiceImpl;

import java.sql.SQLException;
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

@Path("/marketList")
public class MarketListResource {

	private MarketListService marketListService;

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public MarketList create(List<ListItem> listItems, @Context HttpHeaders hh) {
		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {

			throw new CustomUnathorizedException("The user isn't logged in");

		}

		int idUser = Integer.parseInt(hh.getCookies().get("id_user").getValue());
		marketListService = new MarketListServiceImpl();
		return marketListService.create(listItems, idUser);

	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<SupermarketListPrice> getTotals( @Context HttpHeaders hh) {
		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {

			throw new CustomUnathorizedException("The user isn't logged in");

		}

		int idUser = Integer.parseInt(hh.getCookies().get("id_user").getValue());
		marketListService = new MarketListServiceImpl();
		return marketListService.getTotalInSupermarkets(idUser);

	}
	
	@Path("/details")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<MarketListDetails> getDetails(@QueryParam("id_supermarket") int supermarketId, @Context HttpHeaders hh) {
		Map<String, Cookie> pathParams = hh.getCookies();

		if (!pathParams.containsKey("id_user")) {

			throw new CustomUnathorizedException("The user isn't logged in");

		}

		int idUser = Integer.parseInt(hh.getCookies().get("id_user").getValue());
		marketListService = new MarketListServiceImpl();
		return marketListService.getMarketListDetails(supermarketId, idUser);

	}
	
	
	

}

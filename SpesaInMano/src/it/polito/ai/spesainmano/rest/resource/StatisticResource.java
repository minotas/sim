package it.polito.ai.spesainmano.rest.resource;

import java.util.List;
import java.util.Map;
import it.polito.ai.spesainmano.responses.Statistic;
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

@Path("/statistic")
public class StatisticResource {
	private StatisticService statisticService;
	
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

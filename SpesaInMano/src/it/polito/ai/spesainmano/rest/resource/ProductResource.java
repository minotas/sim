package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.exception.CustomUnauthorizedException;
import it.polito.ai.spesainmano.rest.service.ProductService;
import it.polito.ai.spesainmano.rest.serviceimpl.ProductServiceImpl;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/product")
@Produces({ MediaType.APPLICATION_JSON })
public class ProductResource {

	private ProductService productService;

	@POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Product create(Product p, @Context HttpHeaders hh)
    {
		
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
			
			throw new CustomUnauthorizedException("The user isn't logged in");
		
		}
       
 	  
 	   productService= new ProductServiceImpl();
       return productService.create(p);
       
    }

	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public List<Product> getProductsByProductType(@QueryParam("productType") int productTypeId, @Context HttpHeaders hh) {
		
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
		
			throw new CustomUnauthorizedException("The user isn't logged in");
		
		}
		productService = new ProductServiceImpl();
		return productService.getProductByProductType(productTypeId);

	}
	
	@GET
	@Path("/{barcode}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Product getProductByBarcode(@PathParam("barcode") String barcode, @Context HttpHeaders hh) {

		Map<String, Cookie> pathParams = hh.getCookies();
		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}

		productService = new ProductServiceImpl();
		return productService.getProductByBarcode(barcode);

	}

	@Path("/similar")
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public List<Price> getSimilarProducts(@QueryParam("id_product") int productId, @QueryParam("id_supermarket") int supermarketId, @Context HttpHeaders hh) {
		
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
		
			throw new CustomUnauthorizedException("The user isn't logged in");
		
		}
		
		productService = new ProductServiceImpl();
		return productService.getSimilarProducst(productId, supermarketId);

	}
}

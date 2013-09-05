package it.polito.ai.spesainmano.rest.resource;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
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

/**
 * Receives the requests related to the product
 * @version 1.0
 */
@Path("/product")
public class ProductResource {

	private ProductService productService;

	
	/**
	 * Manages the post requests about adding a new product - Requires authentication
	 * 
	 * @param product An object containing the information of the new product
	 * @return a Product object containing the information of the corresponding product, including the id assigned
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomBadRequestException Generated when the information received is not valid or the product has been added
	 * 		   before
	 */
	@POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Product create(Product product, @Context HttpHeaders hh)throws CustomUnauthorizedException, CustomServiceUnavailableException, CustomBadRequestException{
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
       
 	   productService= new ProductServiceImpl();
 	   productService.validate(product);
       return productService.create(product);
       
    }

	/**
	 * Manages the get requests about getting the product related to a product type - Requires authentication
	 * 
	 * @param productTypeId The id of the product type to do the search
	 * @return a List of Products objects containing the information of the corresponding products
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public List<Product> getProductsByProductType(@QueryParam("productType") int productTypeId, @Context HttpHeaders hh) throws CustomUnauthorizedException, CustomServiceUnavailableException{
		
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		productService = new ProductServiceImpl();
		return productService.getProductByProductType(productTypeId);

	}
	
	/**
	 * Manages the get requests about searching a product by its barcode - Requires authentication
	 * 
	 * @param barcode The barcode of the product
	 * @return a Product object containing the information of the corresponding product
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when there barcode does not correspond to any registered product
	 */
	@GET
	@Path("/{barcode}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Product getProductByBarcode(@PathParam("barcode") String barcode, @Context HttpHeaders hh) throws CustomUnauthorizedException, CustomServiceUnavailableException, CustomNotFoundException{

		Map<String, Cookie> pathParams = hh.getCookies();
		if (!pathParams.containsKey("id_user")) {
			throw new CustomUnauthorizedException("The user isn't logged in");
		}

		productService = new ProductServiceImpl();
		return productService.getProductByBarcode(barcode);

	}

	/**
	 * Manages the get requests about searching the similar product of one product in a specific supermarket
	 * Requires authentication 
	 * @param productId The id of the product to search its similar product
	 * @param supermarketId The id of the supermarket to search the similar products
	 * @return a List of prices objects containing the information of the similar products and it current price 
	 * @throws CustomUnauthorizedException Generated when the user is not logged in
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when the productId received does not corresponds to any product 
	 * 		   registered or when there aren't similar product in the supermarket
	 */
	@Path("/similar")
	@GET
	@Produces({ MediaType.APPLICATION_JSON})
	public List<Price> getSimilarProducts(@QueryParam("id_product") int productId, @QueryParam("id_supermarket") int supermarketId, @Context HttpHeaders hh) throws CustomUnauthorizedException, CustomServiceUnavailableException, CustomNotFoundException{
		
		Map<String, Cookie> pathParams = hh.getCookies();
		
		if(!pathParams.containsKey("id_user")){
			throw new CustomUnauthorizedException("The user isn't logged in");
		}
		
		productService = new ProductServiceImpl();
		return productService.getSimilarProducst(productId, supermarketId);

	}
}

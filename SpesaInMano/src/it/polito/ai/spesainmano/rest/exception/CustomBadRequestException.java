package it.polito.ai.spesainmano.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class CustomBadRequestException extends WebApplicationException{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 5411772313826249379L;

	/**
	  * Create a HTTP 400 (Bad Request) exception.
	  * @param message the String that is the entity of the 400 response.
	  */
	  public CustomBadRequestException(String message) {
	    super(Response.status(Status.BAD_REQUEST).entity(message).type("text/plain").build());
	  }
}

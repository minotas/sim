package it.polito.ai.spesainmano.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class CustomUnauthorizedException extends WebApplicationException{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 4153632079795298726L;

	/**
	  * Create a HTTP 401 (Bad Unauthorized) exception.
	  * @param message the String that is the entity of the 401 response.
	  */
	  public CustomUnauthorizedException(String message) {
	    super(Response.status(Status.UNAUTHORIZED).entity(message).type("text/plain").build());
	  }
}

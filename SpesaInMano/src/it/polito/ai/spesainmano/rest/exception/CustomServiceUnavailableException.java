package it.polito.ai.spesainmano.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class CustomServiceUnavailableException extends WebApplicationException{

		  /**
	 * 
	 */
	private static final long serialVersionUID = 8626297657296994121L;

		/**
		  * Create a HTTP 503 (Service Unavailable) exception.
		  * @param message the String that is the entity of the 503 response.
		  */
		  public CustomServiceUnavailableException(String message) {
		    super(Response.status(Status.SERVICE_UNAVAILABLE).entity(message).type("text/plain").build());
		  }
}

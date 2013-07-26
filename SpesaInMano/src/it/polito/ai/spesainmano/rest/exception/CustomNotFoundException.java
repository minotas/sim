package it.polito.ai.spesainmano.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.Responses;

public class CustomNotFoundException extends WebApplicationException {

		  /**
		  * Create a HTTP 404 (Not Found) exception.
		  * @param message the String that is the entity of the 404 response.
		  */
		  public CustomNotFoundException(String message) {
		    super(Response.status(Status.NOT_FOUND).entity(message).type("text/plain").build());
		  }
}
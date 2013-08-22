package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;

import java.util.List;

public interface SupermarketService {

	List<Supermarket> checkIn(float latitude, float longitude) throws CustomBadRequestException, CustomNotFoundException, CustomServiceUnavailableException;

}

package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;

import java.sql.SQLException;
import java.util.List;

public interface PriceService {

	List<Price> insert(Price p)throws CustomServiceUnavailableException;
	
	boolean checkPrice(Price p) throws CustomBadRequestException, CustomServiceUnavailableException;

	float getAverageLastSixMonths(int productId, int supermarketId)throws CustomServiceUnavailableException;
	
	void validate(Price p) throws CustomBadRequestException;
	
}

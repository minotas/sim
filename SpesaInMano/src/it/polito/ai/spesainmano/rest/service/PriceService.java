package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;

import java.sql.SQLException;
import java.util.List;

public interface PriceService {

	List<Price> insert(Price p) throws SQLException;
	
	boolean checkPrice(Price p) throws SQLException, CustomBadRequestException;

	float getAverageLastSixMonths(int productId, int supermarketId) throws SQLException;
	
}

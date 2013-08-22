package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.responses.InsertPriceResponse;
import it.polito.ai.spesainmano.responses.MarketListDetails;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;

import java.sql.SQLException;
import java.util.List;

public interface PriceService {

	InsertPriceResponse insert(Price p)throws CustomServiceUnavailableException;
	
	void checkPrice(Price p) throws CustomBadRequestException, CustomServiceUnavailableException;

	float getAverageLastSixMonths(int productId, int supermarketId)throws CustomServiceUnavailableException;
	
	void validate(Price p) throws CustomBadRequestException;

	List<Price> getNearSupermarketsPrices(int userId, int productId, float latitude, float longitude, int supermarketId);

	List<Price> getOffers(int idUser, float latitude, float longitude) throws CustomServiceUnavailableException;
	
}

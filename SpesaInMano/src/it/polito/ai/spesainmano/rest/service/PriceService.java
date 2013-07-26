package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Price;

import java.sql.SQLException;

public interface PriceService {

	Price insert(Price p) throws SQLException;
	
	boolean checkPrice(Price p) throws SQLException;
	
}

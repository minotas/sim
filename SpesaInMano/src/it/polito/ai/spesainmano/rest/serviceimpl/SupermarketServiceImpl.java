package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.SupermarketDAO;
import it.polito.ai.spesainmano.DAO.SupermarketDAOImpl;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.SupermarketService;

public class SupermarketServiceImpl implements SupermarketService {

	@Override
	public List<Supermarket> checkIn(float latitude, float longitude) throws  CustomBadRequestException, CustomNotFoundException, CustomServiceUnavailableException {
		
		if(latitude < -90 || latitude > 90){
			throw new CustomBadRequestException("Invalid latitude");
		}
		
		if(latitude < -180 || latitude > 180){
			throw new CustomBadRequestException("Invalid longitude");
		}
		
		SupermarketDAO sDAO = new SupermarketDAOImpl();
		
		List<Supermarket> supermarkets;
		
		try {
		
			supermarkets = sDAO.checkin(latitude, longitude);
	
		} catch (SQLException e) {
		
			throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
		
		}
		
		if(supermarkets.size() == 0){
		
			throw new CustomNotFoundException("You aren't in a supermarket");
		
		}
		else{ 
		
			return supermarkets;
		
		}
		
	}

	
	
	
}

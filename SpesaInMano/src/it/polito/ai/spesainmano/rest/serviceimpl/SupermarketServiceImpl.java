package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;
import it.polito.ai.spesainmano.DAO.SupermarketDAO;
import it.polito.ai.spesainmano.DAOImp.SupermarketDAOImpl;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.SupermarketService;

/**
 * Defines the functions related with the supermarkets in the business logic
 * @version 1.0
 */
public class SupermarketServiceImpl implements SupermarketService {

	/**
	 * Implements the logic of the check-in process
	 * 
	 * @param latitude The latitude of the physical position of the user
	 * @param longitude The longitude of the physical position of the user
	 * @return a List of supermarkets located in 100m of radio from the position of the user
	 * @throws CustomBadRequestException Generated when the longitude or the latitude are wrong
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when there isn't any near supermarket
	 */
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
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
		
		if(supermarkets.size() == 0){
			throw new CustomNotFoundException("You aren't in a supermarket");
		}
		else{ 
			return supermarkets;
		}
	}

}

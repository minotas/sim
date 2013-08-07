package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.SupermarketDAO;
import it.polito.ai.spesainmano.DAO.SupermarketDAOImpl;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.service.SupermarketService;

public class SupermarketServiceImpl implements SupermarketService {

	@Override
	public List<Supermarket> checkIn(float latitude, float longitude)throws SQLException, CustomBadRequestException {
		
		if(latitude < -90 || latitude > 90){
			throw new CustomBadRequestException("Invalid latitude");
		}
		
		if(latitude < -180 || latitude > 180){
			throw new CustomBadRequestException("Invalid longitude");
		}
		
		SupermarketDAO sDAO = new SupermarketDAOImpl();
		return sDAO.checkin(latitude, longitude);
		
	}

	
	
	
}

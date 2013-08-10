package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.PriceDAO;
import it.polito.ai.spesainmano.DAO.PriceDAOImp;
import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.PriceService;

public class PriceServiceImpl implements PriceService{

	@Override
	public List<Price> insert(Price p) throws SQLException {
		PriceDAO priceDao = new PriceDAOImp();
        if(priceDao.insert(p) > 0){
        	UserDAO userDao = new UserDAOImp();
        	userDao.incrementPoints(p.getId_user().getId_user());
        	List<Price> prices = priceDao.getProductPriceInNearSupermarkets(p);
        	return prices;
        }
        else{
        	throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
        }
        
	}

	@Override
	public boolean checkPrice(Price p) throws SQLException {
		if(p.getPrice() == 0){
			throw new CustomBadRequestException("The price cannot be 0");
		}
		PriceDAO priceDao = new PriceDAOImp();
		float[] data = priceDao.checkPrice(p);
		float avg, std;
		avg = data[0];
		std = data[1];
		
		if(avg != 0){
			if(p.getPrice()>avg+std || p.getPrice()<avg-std){
	            return false;
			}
		}
		
		return true; 
	}

}

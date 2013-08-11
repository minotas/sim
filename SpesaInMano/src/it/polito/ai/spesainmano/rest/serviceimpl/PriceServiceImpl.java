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
		int credibility, points;
		float price = p.getPrice();
		
		if(p.getPrice() == 0){
			throw new CustomBadRequestException("The price cannot be 0");
		}
		
		UserDAO userDao = new UserDAOImp();
		points = userDao.getPoints(p.getId_user().getId_user());
		if(points < 100){
			credibility = 1;
		}
		else{
			if(points >= 100 && points < 250){
				credibility = 2;
			}
			else{
				credibility = 3;
			}
		}
		
		PriceDAO priceDao = new PriceDAOImp();
		float[] data = priceDao.checkPrice(p);
		
		float avg, std;
		avg = data[0];
		std = data[1];
		
		if(p.getType().equals("n")){
			switch(credibility){
				case 1:if((price > avg + std) || (price < avg - std) || (price > 1.05 * avg)  || (price < avg * 0.95)){
							return false;
						}	
				case 2: if((price > avg + std) || (price < avg - std) || (price > 1.10 * avg)  || (price < avg * 0.90)){
							return false;
						}	
				case 3: if((price > avg + std) || (price < avg - std) || (price > 1.15 * avg)  || (price < avg * 0.85)){
							return false;
					   	}	
				default: return true;
			}
		}
		else{
			switch(credibility){
				case 1:if((price > avg + 2 * std) || (price < avg - 2 * std) || (price > 1.25 * avg)  || (price < avg * 0.75)){
							return false;
						}	
				case 2: if((price > avg + 2 * std) || (price < avg - 2 * std) || (price > 1.35 * avg)  || (price < avg * 0.65)){
							return false;
						}	
				case 3: if((price > avg + 2 * std) || (price < avg - 2 * std) || (price > 1.50 * avg)  || (price < avg * 0.50)){
							return false;
					   	}	
				default: return true;
			}
		}
		
	}

	
	@Override
	public float getAverageLastSixMonths(int productId, int supermarketId) throws SQLException {
		float average;
		PriceDAO priceDao = new PriceDAOImp();
		average = priceDao.getAverageLastSixMonths(productId, supermarketId);
		return average;
	}

}

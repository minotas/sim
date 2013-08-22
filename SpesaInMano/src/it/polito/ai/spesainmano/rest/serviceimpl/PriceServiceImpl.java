package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;

import it.polito.ai.spesainmano.DAO.MarketListDAO;
import it.polito.ai.spesainmano.DAO.MarketListDAOImp;
import it.polito.ai.spesainmano.DAO.PriceDAO;
import it.polito.ai.spesainmano.DAO.PriceDAOImp;
import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAO.UserDAOImp;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.responses.InsertPriceResponse;
import it.polito.ai.spesainmano.responses.MarketListDetails;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.PriceService;

public class PriceServiceImpl implements PriceService{

	@Override
	public void validate(Price p) throws CustomBadRequestException {
		 
		if(p.getId_user().getId_user() == 0 || p.getId_supermarket().getId_supermarket() == 0 || p.getId_product().getId_product() == 0  || p.getPrice() == 0 || p.getType().equals("")){

			throw new CustomBadRequestException("Incomplete Information about the price");  

		}
		
	}
	
	@Override
	public void checkPrice(Price p)throws CustomBadRequestException, CustomServiceUnavailableException{
		int credibility, points;
		float price = p.getPrice();
		
		if(p.getPrice() <= 0){
			throw new CustomBadRequestException("The price must be a positive value");
		}
		
		UserDAO userDao = new UserDAOImp();
		try {
			points = userDao.getPoints(p.getId_user().getId_user());
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		
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
		float[] data;
		try {
			data = priceDao.checkPrice(p);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		
		float avg, std;
		avg = data[0];
		std = data[1];
		
		if(avg != 0){
			
			if(p.getType().equals("n")){
				switch(credibility){
					case 1:if(((price > avg + std) || (price < avg - std)) && ((price > 1.05 * avg)  || (price < avg * 0.95))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
							}	
					case 2: if(((price > avg + std) || (price < avg - std)) && ((price > 1.10 * avg)  || (price < avg * 0.90))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
							}	
					case 3: if(((price > avg + std) || (price < avg - std)) && ((price > 1.15 * avg)  || (price < avg * 0.85))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
						   	}	
					default: return;
				}
			}
			else{
				switch(credibility){
					case 1:if(((price > avg + std) || (price < avg - 2 * std)) && ((price > 1.05 * avg)  || (price < avg * 0.75))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
							}	
					case 2: if(((price > avg + std) || (price < avg - 2 * std)) && ((price > 1.10 * avg)  || (price < avg * 0.65))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
							}	
					case 3: if(((price > avg + std) || (price < avg - 2 * std)) && ((price > 1.15 * avg)  || (price < avg * 0.50))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
						   	}	
					default: return;
				}
			}
			
		}
		
		return;
			
		
	
		
	}

	@Override
	public InsertPriceResponse insert(Price p) throws CustomServiceUnavailableException{
		
		PriceDAO priceDao = new PriceDAOImp();
		float[] priceQuality;
		try {
			if(priceDao.insert(p) > 0){
				InsertPriceResponse r = new InsertPriceResponse();
				UserDAO userDao = new UserDAOImp();
				userDao.incrementPoints(p.getId_user().getId_user());
				r.setUser_score(userDao.getPoints(p.getId_user().getId_user()));
				
				priceQuality = priceDao.getPriceQualityInfo(p.getId_product().getId_product(), p.getId_supermarket().getId_supermarket());
				if(priceQuality[3] == 1){
					r.setPrice_qualifier(5);
				}
				else{
					if(p.getPrice() < priceQuality[1]){
						r.setPrice_qualifier(5);
					}else{
						if(p.getPrice() < priceQuality[0]){
							r.setPrice_qualifier(4);
						}else{
							if(p.getPrice() == priceQuality[0]){
								r.setPrice_qualifier(3);
							}else{
								if(p.getPrice() < priceQuality[2]){
									r.setPrice_qualifier(2);
								}else{
									r.setPrice_qualifier(1);
								}
							}
						}
					}
				}
				r.setId_product(p.getId_product().getId_product());
				return r;
			}
			else{
				throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
			}
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	}

	@Override
	public float getAverageLastSixMonths(int productId, int supermarketId)throws CustomServiceUnavailableException {
		float average;
		PriceDAO priceDao = new PriceDAOImp();
		try {
			average = priceDao.getAverageLastSixMonths(productId, supermarketId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		return average;
	}

	@Override
	public List<Price> getNearSupermarketsPrices(int userId, int productId, float latitude, float longitude, int supermarketId) throws CustomServiceUnavailableException {
		PriceDAO priceDao =  new PriceDAOImp();
		try {
			return priceDao.getProductPriceInNearSupermarkets(userId, productId, latitude, longitude, supermarketId);
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	}

	@Override
	public List<Price> getOffers(int idUser, float latitude, float longitude) {
		MarketListDAO marketListDao = new MarketListDAOImp();
		try {
			int marketListNumber = marketListDao.getNumberOfMarketLists(idUser);
			PriceDAO priceDao = new PriceDAOImp();
			switch(marketListNumber){
			case 0: List<Price> offers = priceDao.getOffersMonitored(idUser, latitude, longitude);
					if(offers.size() > 0){
						return priceDao.getGeneralOffers(longitude, latitude);
					}
					return offers;
			case 1: List<Price> offersOneList = priceDao.getOffersProductsInOneList(idUser, latitude, longitude);
					if(offersOneList.size() > 0){
						return priceDao.getGeneralOffers(longitude, latitude);
					}
					return offersOneList;
			default: List<Price> offersMultipleList = priceDao.getOffersProductsInMultipleLists(idUser, latitude, longitude);
			if(offersMultipleList.size() > 0){
				return priceDao.getGeneralOffers(longitude, latitude);
			}
			return offersMultipleList;
			}
			
		}catch(SQLException sqle){
			throw new CustomServiceUnavailableException("There was an error contacting an upstream server");
		}
	}


}

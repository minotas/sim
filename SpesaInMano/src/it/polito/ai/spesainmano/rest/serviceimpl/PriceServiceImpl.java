package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;
import it.polito.ai.spesainmano.DAO.MarketListDAO;
import it.polito.ai.spesainmano.DAO.PriceDAO;
import it.polito.ai.spesainmano.DAO.UserDAO;
import it.polito.ai.spesainmano.DAOImp.MarketListDAOImp;
import it.polito.ai.spesainmano.DAOImp.PriceDAOImp;
import it.polito.ai.spesainmano.DAOImp.UserDAOImp;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.responses.InsertPriceResponse;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.PriceService;

/**
 * Defines the functions related with the prices in the business logic
 * @version 1.0
 */
public class PriceServiceImpl implements PriceService{

	/**
	 * Validates the information received to create a new price
	 * @param product An object containing the information of the new product
	 * @throws CustomBadRequestException Generated when the information received is not complete 
	*/
	@Override
	public void validate(Price p) throws CustomBadRequestException {
		 
		if(p.getId_user().getId_user() == 0 || p.getId_supermarket().getId_supermarket() == 0 || p.getId_product().getId_product() == 0  || p.getPrice() == 0 || p.getType().equals("")){
			throw new CustomBadRequestException("Incomplete Information about the price");  
		}
		
	}
	
	
	/**
	 * Check that the price follows the politics defined by Spesa in Mano considering
	 *  - The points of the user inserting the price(Credibility)
	 *  - The average and the standard deviation of the last 50 prices of the same product
	 *  - The kind of price(Normal or offer) 
	 * @param product An object containing the information of the new product
	 * @throws CustomBadRequestException Generated when the price does not follows the politics of Spesa in Mano
	 * @throws CustomService Unavailable Generated when there was an error contacting the dbms
	*/
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
			throw new CustomServiceUnavailableException("Server unavailable");
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
			throw new CustomServiceUnavailableException("Server unavailable");
		}
		
		float avg, std;
		avg = data[0];
		std = data[1];
		
		if(avg != 0){
			
			if(p.getType().equals("n")){
				switch(credibility){
					case 1:if(((price > avg + std) || (price < avg - std)) && ((price > 1.15 * avg)  || (price < avg * 0.85))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
							}	
					case 2: if(((price > avg + std) || (price < avg - std)) && ((price > 1.20 * avg)  || (price < avg * 0.80))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
							}	
					case 3: if(((price > avg + std) || (price < avg - std)) && ((price > 1.25 * avg)  || (price < avg * 0.75))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
						   	}	
					default: return;
				}
			}
			else{
				switch(credibility){
					case 1:if(((price > avg + std) || (price < avg - 2 * std)) && ((price > 1.15 * avg)  || (price < avg * 0.75))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
							}	
					case 2: if(((price > avg + std) || (price < avg - 2 * std)) && ((price > 1.20 * avg)  || (price < avg * 0.65))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
							}	
					case 3: if(((price > avg + std) || (price < avg - 2 * std)) && ((price > 1.25 * avg)  || (price < avg * 0.50))){
								throw new CustomBadRequestException("The price cannot be inserted because it seems that is wrong accordingly with our reliability politics");
						   	}	
					default: return;
				}
			}
			
		}
		
		return;
	}

	/**
	 * Implements the logic required to create a new price
	 * @param p A price object containing the information of the new price
	 * @return A InsertPriceResponse object containing the points of the user after the price insertion, the id of the product and
	 * 		   the qualifier of the price considering the mean, the min and the max price of the product  in the supermarkets
	 * 		   in the last 6 months
	 * 		   - 5 if it is the min price
	 * 		   - 4 If it is between the mean and the mean price
	 * 		   - 3 If it is equal to the mean price
	 * 		   - 2 If it is between the mean and the max price
	 * 		   - 1 If it is the max price
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	*/
	@Override
	public InsertPriceResponse insert(Price p) throws CustomServiceUnavailableException{
		
		PriceDAO priceDao = new PriceDAOImp();
		float[] priceQuality;
		try {
			InsertPriceResponse r = new InsertPriceResponse();
			priceQuality = priceDao.getPriceQualityInfo(p.getId_product().getId_product());
			if(priceQuality[3] == 0){
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
		
			if(priceDao.insert(p) > 0){
				UserDAO userDao = new UserDAOImp();
				userDao.incrementPoints(p.getId_user().getId_user());
				r.setUser_score(userDao.getPoints(p.getId_user().getId_user()));
				r.setId_product(p.getId_product().getId_product());
				return r;
			}
			else{
				throw new CustomServiceUnavailableException("Service Unavailable");
			}
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}
	


	/**
	 * Implements the logic required to obtain the prices of the product in the supermarkets located in an area of 1km of radio
	 * and in the monitored supermarkets
	 * @param userId The id of the user to obtain its monitored supermarkets
	 * @param productId The id of the product
	 * @param latitude The latitude of the current position of the user
	 * @param longitude The longitude of the current position of the user
	 * @param supermarketId The id of the supermarket in which the user is
	 * @return The list of the current prices of the product in the near an monitored supermarkets
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when there aren't prices of the product in the near or monitored supermarkets
	*/
	@Override
	public List<Price> getNearSupermarketsPrices(int userId, int productId, float latitude, float longitude, int supermarketId) throws CustomBadRequestException, CustomNotFoundException, CustomServiceUnavailableException {
		if(latitude < -90 || latitude > 90){
			throw new CustomBadRequestException("Invalid latitude");
		}
		
		if(latitude < -180 || latitude > 180){
			throw new CustomBadRequestException("Invalid longitude");
		}
		
		PriceDAO priceDao =  new PriceDAOImp();
		
		try {
			List<Price> prices =  priceDao.getProductPriceInNearSupermarkets(userId, productId, latitude, longitude, supermarketId);
		
			if(prices.size() == 0){
				throw new CustomNotFoundException("The product is not present in any near supermarket");
			}
			return prices;
		} catch (SQLException e) {
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}


	/**
	 * Implements the logic required to obtain the offers considering:
	 * - The number of market list that have the user
	 * - Monitored Products
	 * - Monitored Supermarkets
	 * - Favourite Supermarkets
	 * - Favourite Products 
	 * - Position of the user
	 * @param userId The id of the user
	 * @param latitude The latitude of the current position of the user
	 * @param longitude The longitude of the current position of the user
	 * @return - If the user does not have any market list it returns the following offers
	 * 				* Monitored products
	 * 				* Monitored supermarkets
	 * 				* Favourite Supermarkets
	 * 				* Or general offers
	 *			- If the user has market lists it returns the following offers
	 *				- Favourite Supermarkets
	 *			    - Favourite Products 
	 *				* Monitored products
	 * 				* Monitored supermarkets
	 * 				* Or general offers
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when there aren't prices of the product in the near or monitored supermarkets
	*/
	@Override
	public List<Price> getOffers(int idUser, float latitude, float longitude)throws CustomBadRequestException, CustomNotFoundException, CustomServiceUnavailableException{
		if(latitude < -90 || latitude > 90){
			throw new CustomBadRequestException("Invalid latitude");
		}
		
		if(latitude < -180 || latitude > 180){
			throw new CustomBadRequestException("Invalid longitude");
		}
		MarketListDAO marketListDao = new MarketListDAOImp();
		try {
			int marketListNumber = marketListDao.getNumberOfMarketLists(idUser);
			PriceDAO priceDao = new PriceDAOImp();
			switch(marketListNumber){
			case 0: List<Price> offers = priceDao.getOffersMonitored(idUser, latitude, longitude);
					if(offers.size() == 0){
						List<Price> generalOffers = priceDao.getGeneralOffers(longitude, latitude); 
						if(generalOffers.size() == 0){
							throw new CustomNotFoundException("Offers not found");
						}
						else return generalOffers;
					}
					return offers;
			case 1: List<Price> offersOneList = priceDao.getOffersProductsInOneList(idUser, latitude, longitude);
					if(offersOneList.size() == 0){
						List<Price> generalOffers = priceDao.getGeneralOffers(longitude, latitude); 
						if(generalOffers.size() == 0){
							throw new CustomNotFoundException("Offers not found");
						}
						else return generalOffers;
					}
					return offersOneList;
			default: List<Price> offersMultipleList = priceDao.getOffersProductsInMultipleLists(idUser, latitude, longitude);
			if(offersMultipleList.size() == 0){
				List<Price> generalOffers = priceDao.getGeneralOffers(longitude, latitude); 
				if(generalOffers.size() == 0){
					throw new CustomNotFoundException("Offers not found");
				}
				else return generalOffers;
			}
			return offersMultipleList;
			}
			
		}catch(SQLException sqle){
			throw new CustomServiceUnavailableException("Service Unavailable");
		}
	}
}

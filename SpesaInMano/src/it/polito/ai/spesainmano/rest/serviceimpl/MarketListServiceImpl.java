package it.polito.ai.spesainmano.rest.serviceimpl;

import it.polito.ai.spesainmano.DAO.ListItemDAO;
import it.polito.ai.spesainmano.DAO.MarketListDAO;
import it.polito.ai.spesainmano.DAO.MonitoredSupermarketDAO;
import it.polito.ai.spesainmano.DAO.SupermarketDAO;
import it.polito.ai.spesainmano.DAOImp.ListItemDAOImp;
import it.polito.ai.spesainmano.DAOImp.MarketListDAOImp;
import it.polito.ai.spesainmano.DAOImp.MonitoredSupermarketDAOImp;
import it.polito.ai.spesainmano.DAOImp.SupermarketDAOImpl;
import it.polito.ai.spesainmano.model.ListItem;
import it.polito.ai.spesainmano.model.MarketList;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.responses.MarketListDetail;
import it.polito.ai.spesainmano.responses.SupermarketListPrice;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.MarketListService;

import java.sql.SQLException;
import java.util.List;

/**
 * Defines the functions related with the market lists in the business logic
 * @version 1.0
 */
public class MarketListServiceImpl implements MarketListService{
	
	/**
	 * Creates a new market list for the user
	 * @param listItems A list of items selected by the user in his market list.
	 * @param userId The id of the user that creates the list
	 * @return A MarketList Object containing the id of the new market list created.
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	*/
	@Override
	public MarketList create(List<ListItem> listItems, int userId) {
		MarketListDAO marketListDao = new MarketListDAOImp();
		MarketList ml = new MarketList();
		try {
			int listId = marketListDao.insert(userId);
			ml.setId(listId);
			ListItemDAO listItemDao = new ListItemDAOImp();
			listItemDao.insertListItems(listItems, listId);
			return ml;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		
	}

	/**
	 * Obtains the total price and products found of a market list in the most visited and monitored supermarkets 
	 * @param userId The id of the user getting the request
	 * @return A list of SupermarketListPrice Objects containing the number of products found, the price of the
	 * 		   market list in the most visited and monitored supermarkets.
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when the user doesn't have a market list
	*/
	@Override
	public List<SupermarketListPrice> getTotalInSupermarkets(int userId) throws CustomServiceUnavailableException, CustomNotFoundException{
		try{
			
			MarketListDAO marketListDao = new MarketListDAOImp();
			int marketListId = marketListDao.getLastMarketListId(userId);
			
			if(marketListId == 0){
				throw new CustomNotFoundException("You don't have any market list");
			}
			
			SupermarketDAO supermarketDao = new SupermarketDAOImpl();
			List<Supermarket> favoriteSupermarkets = supermarketDao.getMostVisitedSupermarkets(userId);
			MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
			
			favoriteSupermarkets.addAll(monitoredSupermarketDao.getMonitoredSupermarkets(userId));
			return marketListDao.getTotal(favoriteSupermarkets, marketListId);
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	}

	/**
	 * Obtains the details of the last market list of the user in a specific supermarket 
	 * @param userId The id of the user doing the request
	 * @param supermarketId The id of the supermarket in which will be obtained the market list details
	 * @return A list of MarketListDetail Objects the info about the products an their prices in the supermarket
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomNotFoundException Generated when the user doesn't have a market list
	*/
	@Override
	public List<MarketListDetail> getMarketListDetails(int supermarketId,	int userId) throws CustomServiceUnavailableException, CustomNotFoundException{
		
		MarketListDAO marketListDao = new MarketListDAOImp();
		int marketListId;
		
		try {
			marketListId = marketListDao.getLastMarketListId(userId);
			
			if(marketListId == 0){
				throw new CustomNotFoundException("You don't have any market list");
			}
			
			return marketListDao.getDetails(supermarketId, marketListId);
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		
	}

	/**
	 * Validates the quantities of the list items of a market list
	 * @param listItems A list of items selected by the user in his market list.
	 * @throws CustomServiceUnavailableException Generated when the service is not available
	 * @throws CustomBadRequestException Generated when the quantity of one list item is not a positive integer
	*/
	@Override
	public void validateMarketList(List<ListItem> listItems) throws CustomBadRequestException {
		int listSize = listItems.size();
		for(int i = 0; i < listSize; i++){
			if(listItems.get(i).getQuantity() <= 0){
				throw new CustomBadRequestException("The quantity of a product is invalid");
			}
		}
		
	}
	
	

	
}

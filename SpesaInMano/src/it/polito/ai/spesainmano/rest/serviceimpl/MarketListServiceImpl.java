package it.polito.ai.spesainmano.rest.serviceimpl;

import it.polito.ai.spesainmano.DAO.ListItemDAO;
import it.polito.ai.spesainmano.DAO.ListItemDAOImp;
import it.polito.ai.spesainmano.DAO.MarketListDAO;
import it.polito.ai.spesainmano.DAO.MarketListDAOImp;
import it.polito.ai.spesainmano.DAO.MonitoredSupermarketDAO;
import it.polito.ai.spesainmano.DAO.MonitoredSupermarketDAOImp;
import it.polito.ai.spesainmano.DAO.SupermarketDAO;
import it.polito.ai.spesainmano.DAO.SupermarketDAOImpl;
import it.polito.ai.spesainmano.model.ListItem;
import it.polito.ai.spesainmano.model.MarketList;
import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.responses.MarketListDetails;
import it.polito.ai.spesainmano.responses.SupermarketListPrice;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.MarketListService;

import java.sql.SQLException;
import java.util.List;

public class MarketListServiceImpl implements MarketListService{
	

	@Override
	public MarketList create(List<ListItem> listItems, int idUser) {
		MarketListDAO marketListDao = new MarketListDAOImp();
		MarketList ml = new MarketList();
		try {
			int idList = marketListDao.insert(idUser);
			ml.setId(idList);
			ListItemDAO listItemDao = new ListItemDAOImp();
			listItemDao.insertListItems(listItems, idList);
			return ml;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
		
	}

	@Override
	public List<SupermarketListPrice> getTotalInSupermarkets(int idUser) {
		try{
			
			MarketListDAO marketListDao = new MarketListDAOImp();
			int marketListId = marketListDao.getLastMarketListId(idUser);
			if(marketListId == 0){
				throw new CustomNotFoundException("You don't have any market list");
			}
			SupermarketDAO supermarketDao = new SupermarketDAOImpl();
			List<Supermarket> favoriteSupermarkets = supermarketDao.getMostVisitedSupermarkets(idUser);
			MonitoredSupermarketDAO monitoredSupermarketDao = new MonitoredSupermarketDAOImp();
			
			favoriteSupermarkets.addAll(monitoredSupermarketDao.getMonitoredSupermarkets(idUser));
			return marketListDao.getTotal(favoriteSupermarkets, marketListId);
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	}

	@Override
	public List<MarketListDetails> getMarketListDetails(int supermarketId,	int userId) {
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
	
	

	
}

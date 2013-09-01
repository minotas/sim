package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.ListItem;
import it.polito.ai.spesainmano.model.MarketList;
import it.polito.ai.spesainmano.responses.MarketListDetail;
import it.polito.ai.spesainmano.responses.SupermarketListPrice;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;

import java.util.List;

public interface MarketListService {
	
	public MarketList create(List<ListItem> listItems, int idUser);

	public List<SupermarketListPrice> getTotalInSupermarkets(int userId) throws CustomServiceUnavailableException, CustomNotFoundException;

	public List<MarketListDetail> getMarketListDetails(int supermarketId, int userId);
	
	public void validateMarketList(List<ListItem> listItems) throws CustomBadRequestException;

}
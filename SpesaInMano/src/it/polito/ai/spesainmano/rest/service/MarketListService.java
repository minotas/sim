package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.ListItem;
import it.polito.ai.spesainmano.model.MarketList;
import it.polito.ai.spesainmano.responses.MarketListDetails;
import it.polito.ai.spesainmano.responses.SupermarketListPrice;

import java.util.List;

public interface MarketListService {
	
	public MarketList create(List<ListItem> listItems, int idUser);

	public List<SupermarketListPrice> getTotalInSupermarkets(int userId);

	public List<MarketListDetails> getMarketListDetails(int supermarketId, int userId);

}
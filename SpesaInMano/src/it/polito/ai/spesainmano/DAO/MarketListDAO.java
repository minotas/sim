package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.responses.MarketListDetail;
import it.polito.ai.spesainmano.responses.SupermarketListPrice;

import java.sql.SQLException;
import java.util.List;

public interface MarketListDAO{
	
	public int insert(int idUser) throws SQLException;
	
	public List<SupermarketListPrice> getTotal(List<Supermarket> importantSupermarkets, int marketList) throws SQLException;
	
	public int getNumberOfMarketLists(int idUser) throws SQLException;

	public int getLastMarketListId(int idUser) throws SQLException;

	public List<MarketListDetail> getDetails(int supermaketId, int marketListId) throws SQLException;

}

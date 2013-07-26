package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;

import it.polito.ai.spesainmano.DAO.PriceDAO;
import it.polito.ai.spesainmano.DAO.PriceDAOImp;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.rest.service.PriceService;

public class PriceServiceImpl implements PriceService{

	@Override
	public Price insert(Price p) throws SQLException {
		PriceDAO priceDao = new PriceDAOImp();
        return priceDao.insert(p);
	}

	@Override
	public boolean checkPrice(Price p) throws SQLException {
		PriceDAO priceDao = new PriceDAOImp();
		return priceDao.checkPrice(p);
	}

}

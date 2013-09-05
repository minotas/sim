package it.polito.ai.spesainmano.rest.serviceimpl;

import java.sql.SQLException;
import java.util.List;
import it.polito.ai.spesainmano.DAO.PriceDAO;
import it.polito.ai.spesainmano.DAOImp.PriceDAOImp;
import it.polito.ai.spesainmano.responses.Statistic;
import it.polito.ai.spesainmano.rest.exception.CustomNotFoundException;
import it.polito.ai.spesainmano.rest.exception.CustomServiceUnavailableException;
import it.polito.ai.spesainmano.rest.service.StatisticService;

public class StatisticServiceImpl implements StatisticService {

	
	@Override
	public List<Statistic> getAveragesLastSixMonths(int id_supermaket, int id_product) {
		PriceDAO statisticDao = new PriceDAOImp();
		try {
			List<Statistic> statistics = statisticDao.getAverageLastSixMonths(id_supermaket, id_product);
			if(statistics.size() == 0){
				throw new CustomNotFoundException("No statistics found");
			}
			return statistics;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new CustomServiceUnavailableException("Server received an invalid response from upstream server");
		}
	}

}

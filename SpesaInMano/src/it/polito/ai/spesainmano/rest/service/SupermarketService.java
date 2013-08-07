package it.polito.ai.spesainmano.rest.service;

import it.polito.ai.spesainmano.model.Supermarket;
import it.polito.ai.spesainmano.rest.exception.CustomBadRequestException;
import java.sql.SQLException;
import java.util.List;

public interface SupermarketService {

	List<Supermarket> checkIn(float latitude, float longitude) throws SQLException, CustomBadRequestException;
}

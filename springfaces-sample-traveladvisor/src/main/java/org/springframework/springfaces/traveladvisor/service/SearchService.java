package org.springframework.springfaces.traveladvisor.service;

import org.springframework.data.domain.Page;
import org.springframework.springfaces.traveladvisor.domain.City;

public interface SearchService {

	Page<City> findCityByName(String name);

	boolean isCityNameExactMatch(String name);

}

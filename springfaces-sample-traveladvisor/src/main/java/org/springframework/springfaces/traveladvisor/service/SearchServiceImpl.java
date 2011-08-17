package org.springframework.springfaces.traveladvisor.service;

import org.springframework.data.domain.Page;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.stereotype.Component;

@Component
public class SearchServiceImpl implements SearchService {

	public Page<City> findCityByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCityNameExactMatch(String name) {
		return false;
	}

}

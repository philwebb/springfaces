package org.springframework.springfaces.traveladvisor.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.service.CitySearchCriteria;
import org.springframework.springfaces.traveladvisor.service.CityService;
import org.springframework.stereotype.Component;

@Component
public class CityAutoComplete {

	private static final Pageable PAGEABLE = new PageRequest(0, 30);

	private CityService cityService;

	public List<City> suggest(String name) {
		Page<City> suggestions = cityService.findCities(new CitySearchCriteria(name), PAGEABLE);
		return suggestions.getContent();
	}

	@Autowired
	public void setCityService(CityService cityService) {
		this.cityService = cityService;
	}

}

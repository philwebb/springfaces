package org.springframework.springfaces.traveladvisor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.stereotype.Component;

@Component("cityService")
public class CityServiceImpl implements CityService {

	public Page<City> findCities(CitySearchCriteria criteria, Pageable pageable) {
		List<City> content = new ArrayList<City>();
		content.add(new City("Bath", "UK"));
		if (!"exact".equals(criteria.getName())) {
			content.add(new City("London", "UK"));
		}
		return new PageImpl<City>(content);
	}

	// FIXME consider making country a domain specific type
	public City getCity(String name, String country) {
		return new City(name, country);
	}

	public Page<HotelSummary> getHotels(City city, Pageable pageable) {
		List<HotelSummary> content = new ArrayList<HotelSummary>();
		content.add(new HotelSummary("the-royal-hotel-bath", "The Royal Hotel", 4));
		content.add(new HotelSummary("willow-lodge-bath", "Willow Lodge", 2));
		return new PageImpl<HotelSummary>(content);
	}
}

package org.springframework.springfaces.traveladvisor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.CityRepository;
import org.springframework.springfaces.traveladvisor.domain.HotelRepository;
import org.springframework.springfaces.traveladvisor.domain.HotelSummary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Component("cityService")
@Transactional
public class CityServiceImpl implements CityService {

	// FIXME deal with null repository return values

	private CityRepository cityRepository;

	private HotelRepository hotelRepository;

	public Page<City> findCities(CitySearchCriteria criteria, Pageable pageable) {
		Assert.notNull(criteria, "Criteria must not be null");
		String name = criteria.getName();
		String country = "";
		int splitPos = name.lastIndexOf(",");
		if (splitPos >= 0) {
			country = name.substring(splitPos + 1);
			name = name.substring(0, splitPos);
		}
		name = "%" + name.trim() + "%";
		country = "%" + country.trim() + "%";
		return cityRepository.findByNameAndCountryLikeAllIgnoringCase(name, country, pageable);
	}

	public City getCity(String name, String country) {
		Assert.notNull(name, "Name must not be null");
		Assert.notNull(country, "Country must not be null");
		return cityRepository.findByNameAndCountry(name, country);
	}

	public Page<HotelSummary> getHotels(City city, Pageable pageable) {
		Assert.notNull(city, "City must not be null");
		return hotelRepository.findByCity(city, pageable);
	}

	@Autowired
	public void setCityRepository(CityRepository ciryRepository) {
		this.cityRepository = ciryRepository;
	}

	@Autowired
	public void setHotelRepository(HotelRepository hotelRepository) {
		this.hotelRepository = hotelRepository;
	}
}

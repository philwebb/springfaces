/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.traveladvisor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.HotelSummary;
import org.springframework.springfaces.traveladvisor.domain.repository.CityRepository;
import org.springframework.springfaces.traveladvisor.domain.repository.HotelSummaryRepository;
import org.springframework.springfaces.traveladvisor.service.CitySearchCriteria;
import org.springframework.springfaces.traveladvisor.service.CityService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Component("cityService")
@Transactional
public class CityServiceImpl implements CityService {

	// FIXME deal with null repository return values

	private CityRepository cityRepository;

	private HotelSummaryRepository hotelSummaryRepository;

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
		return hotelSummaryRepository.findByCity(city, pageable);
	}

	@Autowired
	public void setCityRepository(CityRepository ciryRepository) {
		this.cityRepository = ciryRepository;
	}

	@Autowired
	public void setHotelSummaryRepository(HotelSummaryRepository hotelSummaryRepository) {
		this.hotelSummaryRepository = hotelSummaryRepository;
	}
}

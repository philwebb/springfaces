package org.springframework.springfaces.traveladvisor.service.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.HotelSummary;
import org.springframework.springfaces.traveladvisor.domain.repository.CityRepository;
import org.springframework.springfaces.traveladvisor.domain.repository.HotelRepository;
import org.springframework.springfaces.traveladvisor.service.CitySearchCriteria;
import org.springframework.springfaces.traveladvisor.service.impl.CityServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class CityServiceImplTest {

	@InjectMocks
	private CityServiceImpl cityService = new CityServiceImpl();;

	@Mock
	private CityRepository cityRepository;

	@Mock
	private HotelRepository hotelRepository;

	@Mock
	private Page<City> cities;

	@Mock
	private City city;

	@Mock
	private Page<HotelSummary> hotels;

	@Mock
	private Pageable pageable;

	@Test
	public void shouldFindCitiesByName() throws Exception {
		CitySearchCriteria criteria = new CitySearchCriteria("city");
		given(cityRepository.findByNameAndCountryLikeAllIgnoringCase("%city%", "%%", pageable)).willReturn(cities);
		assertThat(cityService.findCities(criteria, pageable), is(cities));
	}

	@Test
	public void shouldFindCitiesByNameRestrictedToCountry() throws Exception {
		CitySearchCriteria criteria = new CitySearchCriteria("city, country");
		given(cityRepository.findByNameAndCountryLikeAllIgnoringCase("%city%", "%country%", pageable)).willReturn(
				cities);
		assertThat(cityService.findCities(criteria, pageable), is(cities));
	}

	@Test
	public void shouldFindCitiesByNameRestrictedToCountryWithExtraCommas() throws Exception {
		CitySearchCriteria criteria = new CitySearchCriteria("ci,ty,country");
		given(cityRepository.findByNameAndCountryLikeAllIgnoringCase("%ci,ty%", "%country%", pageable)).willReturn(
				cities);
		assertThat(cityService.findCities(criteria, pageable), is(cities));
	}

	@Test
	public void shouldFindCitiesByCountry() throws Exception {
		CitySearchCriteria criteria = new CitySearchCriteria(",country");
		given(cityRepository.findByNameAndCountryLikeAllIgnoringCase("%%", "%country%", pageable)).willReturn(cities);
		assertThat(cityService.findCities(criteria, pageable), is(cities));
	}

	@Test
	public void shouldGetCity() throws Exception {
		given(cityRepository.findByNameAndCountry("name", "country")).willReturn(city);
		assertThat(cityService.getCity("name", "country"), is(city));
	}

	@Test
	public void shouldGetHotels() throws Exception {
		given(hotelRepository.findByCity(city, pageable)).willReturn(hotels);
		assertThat(cityService.getHotels(city, pageable), is(hotels));
	}
}

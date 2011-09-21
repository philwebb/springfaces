package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.traveladvisor.integrationtest.page.CityPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.CitySearchPage;

public class CitySearchIT {

	@Rule
	public TravelAdvisorPages pages = new TravelAdvisorPages();

	@Test
	public void shouldFindSingleCity() throws Exception {
		CitySearchPage citySearchPage = pages.get(CitySearchPage.class);
		CityPage found = citySearchPage.searchForSingleCity("Bath");
		assertThat(found, is(notNullValue()));
	}
}

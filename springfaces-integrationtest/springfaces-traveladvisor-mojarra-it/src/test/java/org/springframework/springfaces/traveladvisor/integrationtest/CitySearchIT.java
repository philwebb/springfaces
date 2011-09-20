package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.Page;
import org.springframework.springfaces.integrationtest.selenium.SeleniumTest;
import org.springframework.springfaces.traveladvisor.integrationtest.page.CityPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.CitySearchPage;

public class CitySearchIT extends SeleniumTest {

	@Page("/springfaces-traveladvisor/spring/advisor/cities/search")
	CitySearchPage citySearchPage;

	@Test
	public void shouldFindSingleCity() throws Exception {
		CityPage found = citySearchPage.searchForSingleCity("Bath");
		assertThat(found, is(notNullValue()));
	}
}

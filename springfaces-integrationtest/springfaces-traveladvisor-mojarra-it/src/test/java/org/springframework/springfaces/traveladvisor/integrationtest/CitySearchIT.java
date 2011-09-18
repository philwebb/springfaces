package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.springfaces.traveladvisor.integrationtest.page.CityPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.CitySearchPage;

public class CitySearchIT {

	@Test
	public void shouldFindSingleCity() throws Exception {
		WebDriver webDriver = new FirefoxDriver();
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		try {
			webDriver.get("http://localhost:8080/springfaces-traveladvisor/spring/advisor/cities/search");
			CitySearchPage citySearchPage = new CitySearchPage(webDriver);
			CityPage found = citySearchPage.searchForSingleCity("Bath");
			assertThat(found, is(notNullValue()));
		} finally {
			webDriver.quit();
		}
	}
}

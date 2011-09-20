package org.springframework.springfaces.traveladvisor.integrationtest.page;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;

public class CityPage extends PageObject {

	public CityPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.endsWith("City");
	}
}

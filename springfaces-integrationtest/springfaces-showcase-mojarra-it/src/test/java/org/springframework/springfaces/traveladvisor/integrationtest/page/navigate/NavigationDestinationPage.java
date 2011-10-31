package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;

public class NavigationDestinationPage extends PageObject {

	public NavigationDestinationPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Destination");
	}
}

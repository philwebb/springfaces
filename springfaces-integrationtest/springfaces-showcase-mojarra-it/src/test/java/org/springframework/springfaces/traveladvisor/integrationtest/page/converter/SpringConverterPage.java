package org.springframework.springfaces.traveladvisor.integrationtest.page.converter;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/converter/springbean?value=123")
public class SpringConverterPage extends PageObject {

	public SpringConverterPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Converter - Spring (by ID)");
	}
}

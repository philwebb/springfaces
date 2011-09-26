package org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/requestmapping/string")
public class StringRequestMappingPage extends PageObject {

	public StringRequestMappingPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("RequestMapping - Mapped By Name");
	}
}

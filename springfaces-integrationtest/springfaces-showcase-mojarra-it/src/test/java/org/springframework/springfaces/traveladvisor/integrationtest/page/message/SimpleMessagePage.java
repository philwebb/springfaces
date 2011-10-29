package org.springframework.springfaces.traveladvisor.integrationtest.page.message;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/message/simple")
public class SimpleMessagePage extends PageObject {

	public SimpleMessagePage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Message - Simple");
	}
}

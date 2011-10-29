package org.springframework.springfaces.traveladvisor.integrationtest.page.message;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/message/prefix")
public class PrefixMessagePage extends PageObject {

	public PrefixMessagePage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Message - Prefix");
	}
}

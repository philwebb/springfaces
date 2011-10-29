package org.springframework.springfaces.traveladvisor.integrationtest.page.message;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/message/objects")
public class ObjectsMessagePage extends PageObject {

	public ObjectsMessagePage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Message - Objects");
	}
}

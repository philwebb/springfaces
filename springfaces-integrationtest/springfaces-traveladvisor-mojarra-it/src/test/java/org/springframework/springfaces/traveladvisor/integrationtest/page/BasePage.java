package org.springframework.springfaces.traveladvisor.integrationtest.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.util.Assert;

public abstract class BasePage extends PageObject {

	public BasePage(WebDriver webDriver) {
		super(webDriver);
	}

	protected final WebElement formElementById(String id) {
		Assert.notNull(id, "ID must not be null");
		return findElementById("form:" + id);
	}
}

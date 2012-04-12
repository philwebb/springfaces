package org.springframework.springfaces.traveladvisor.integrationtest.page.template;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/template/decorateall")
public class DecorateAllPage extends PageObject {

	public DecorateAllPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return "Templates - Decorate All".equals(title);
	}

	public String getText() {
		return getWebDriver().findElement(By.tagName("body")).getText();
	}

}

package org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;

public abstract class AbstractSelectItemsPageObject extends PageObject {

	public AbstractSelectItemsPageObject(WebDriver webDriver) {
		super(webDriver);
	}

	public AbstractSelectItemsPageObject clickSubmitButton() {
		getWebDriver().findElement(By.id("form:submit")).click();
		return newPage(getClass());
	}
}

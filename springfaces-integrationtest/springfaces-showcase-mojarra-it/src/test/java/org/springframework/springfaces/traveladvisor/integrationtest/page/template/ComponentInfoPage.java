package org.springframework.springfaces.traveladvisor.integrationtest.page.template;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/template/componentinfo")
public class ComponentInfoPage extends PageObject {

	public ComponentInfoPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return "Templates - Component Info".equals(title);
	}

	public String getDetails() {
		return getWebDriver().findElement(By.id("form:details")).getText();
	}

	public ComponentInfoPage clickSubmitButton() {
		getWebDriver().findElement(By.id("form:submit")).click();
		return newPage(getClass());
	}

}

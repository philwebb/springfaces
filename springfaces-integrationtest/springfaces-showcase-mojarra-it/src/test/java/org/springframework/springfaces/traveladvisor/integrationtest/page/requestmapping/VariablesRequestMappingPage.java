package org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;

public class VariablesRequestMappingPage extends PageObject {

	public VariablesRequestMappingPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("RequestMapping - Variables");
	}

	public String getPathText() {
		return getWebDriver().findElement(By.id("pathText")).getText();
	}

	public String getArgumentText() {
		return getWebDriver().findElement(By.id("argumentText")).getText();
	}
}

package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/rulecommandlink")
public class NavigateRuleCommandLinkPage extends PageObject {

	public NavigateRuleCommandLinkPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Rule Command Link");
	}

	public NavigationDestinationPage click() {
		getWebDriver().findElement(By.id("form:commandlink")).click();
		return newPage(NavigationDestinationPage.class);
	}
}

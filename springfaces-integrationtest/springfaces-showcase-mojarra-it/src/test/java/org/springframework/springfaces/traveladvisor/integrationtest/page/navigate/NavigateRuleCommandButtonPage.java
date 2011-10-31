package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/rulecommandbutton")
public class NavigateRuleCommandButtonPage extends PageObject {

	public NavigateRuleCommandButtonPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Rule Command Button");
	}

	public NavigationDestinationPage click() {
		getWebDriver().findElement(By.id("form:commandbutton")).click();
		return newPage(NavigationDestinationPage.class);
	}
}

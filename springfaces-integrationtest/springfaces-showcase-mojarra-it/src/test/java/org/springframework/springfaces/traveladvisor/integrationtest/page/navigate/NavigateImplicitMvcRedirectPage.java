package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/implicitmvcredirect")
public class NavigateImplicitMvcRedirectPage extends PageObject {

	public NavigateImplicitMvcRedirectPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Implicit MVC Redirect");
	}

	public NavigationDestinationPage click() {
		getWebDriver().findElement(By.id("form:link")).click();
		return newPage(NavigationDestinationPage.class);
	}
}

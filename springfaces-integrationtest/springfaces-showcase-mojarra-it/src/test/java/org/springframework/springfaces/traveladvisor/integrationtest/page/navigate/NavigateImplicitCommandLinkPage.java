package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/implicitcommandlink")
public class NavigateImplicitCommandLinkPage extends PageObject {

	public NavigateImplicitCommandLinkPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Implicit Command Link");
	}

	public NavigationDestinationPage click() {
		getWebDriver().findElement(By.id("form:commandlink")).click();
		return newPage(NavigationDestinationPage.class);
	}
}

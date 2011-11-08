package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/annotationlink")
public class NavigateAnnotationLinkPage extends PageObject {

	public NavigateAnnotationLinkPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Annotation Link");
	}

	public NavigationDestinationPage click() {
		getWebDriver().findElement(By.id("form:link")).click();
		return newPage(NavigationDestinationPage.class);
	}
}
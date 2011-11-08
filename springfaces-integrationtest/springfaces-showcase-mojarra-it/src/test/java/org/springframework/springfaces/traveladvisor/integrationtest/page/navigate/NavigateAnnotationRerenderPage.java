package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/annotationrerender")
public class NavigateAnnotationRerenderPage extends PageObject {

	public NavigateAnnotationRerenderPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Annotation Re-Render");
	}

	public String getText() {
		return getWebDriver().findElement(By.id("form:text")).getText();
	}

	public NavigateAnnotationRerenderPage click() {
		getWebDriver().findElement(By.id("form:button")).click();
		return newPage(NavigateAnnotationRerenderPage.class);
	}
}
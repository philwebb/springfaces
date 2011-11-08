package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/annotationresponsebody")
public class NavigateAnnotationResponseBodyPage extends PageObject {

	public NavigateAnnotationResponseBodyPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Annotation Response Body");
	}

	public String click() {
		getWebDriver().findElement(By.id("form:button")).click();
		return getBodyText();
	}
}
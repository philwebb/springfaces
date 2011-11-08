package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/annotationstreaming")
public class NavigateAnnotationStreamingPage extends PageObject {

	public NavigateAnnotationStreamingPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Annotation Streaming");
	}

	public String click() {
		getWebDriver().findElement(By.id("form:button")).click();
		return getBodyText();
	}
}
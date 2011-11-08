package org.springframework.springfaces.traveladvisor.integrationtest.page.navigate;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/navigation/annotationwithvalue")
public class NavigateAnnotationWithValuePage extends PageObject {

	public NavigateAnnotationWithValuePage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Navigation - Annotation With Value");
	}

	public void setInputText(String value) {
		WebElement inputText = getWebDriver().findElement(By.name("form:inputtext"));
		inputText.clear();
		inputText.sendKeys(value);
	}

	public NavigationDestinationPage click() {
		getWebDriver().findElement(By.id("form:button")).click();
		return newPage(NavigationDestinationPage.class);
	}
}
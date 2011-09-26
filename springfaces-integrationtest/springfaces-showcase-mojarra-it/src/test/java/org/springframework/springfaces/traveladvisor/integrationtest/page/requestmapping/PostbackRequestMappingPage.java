package org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/requestmapping/postback")
public class PostbackRequestMappingPage extends PageObject {

	public PostbackRequestMappingPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("RequestMapping - Postback");
	}

	public long getDate() {
		String dateText = getWebDriver().findElement(By.id("dateText")).getText();
		return Long.valueOf(dateText);
	}

	public PostbackRequestMappingPage clickPostbackButton() {
		getWebDriver().findElement(By.id("form:postback")).click();
		return newPage(PostbackRequestMappingPage.class);
	}

}

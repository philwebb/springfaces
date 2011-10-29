package org.springframework.springfaces.traveladvisor.integrationtest.page.message;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/message/missing")
public class MissingMessagePage extends PageObject {

	public MissingMessagePage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Message - Missing");
	}

	public String getOutputText() {
		return getWebDriver().findElement(By.id("output")).getText();
	}

	public String getErrorMessage() {
		return getWebDriver().findElement(By.xpath("/html/body/ul/li")).getText().trim();
	}
}

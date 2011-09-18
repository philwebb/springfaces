package org.springframework.springfaces.traveladvisor.integrationtest.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CitySearchPage extends BasePage {

	public CitySearchPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.endsWith("Search");
	}

	public CityPage searchForSingleCity(String query) {
		WebElement input = getInputElement();
		input.sendKeys(query);
		waitOnUrlChange(getSearchButton()).click();
		return newPage(CityPage.class);
	}

	private WebElement getInputElement() {
		return formElementById("query_input");
	}

	private WebElement getSearchButton() {
		return formElementById("search");
	}
}

package org.springframework.springfaces.traveladvisor.integrationtest.page.validator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;

public abstract class AbstractValidatorPageObject extends PageObject {

	public AbstractValidatorPageObject(WebDriver webDriver) {
		super(webDriver);
	}

	public void setInputText(CharSequence value) {
		getWebDriver().findElement(By.id("form:input")).sendKeys(value);
	}

	public AbstractValidatorPageObject clickSubmitButton() {
		getWebDriver().findElement(By.id("form:submit")).click();
		return newPage(getClass());
	}

	public String getErrorMessage() {
		return getWebDriver().findElement(By.xpath("/html/body/ul/li")).getText().trim();
	}

	public boolean hasError() {
		return getWebDriver().getPageSource().indexOf("ul") != -1;
	}
}

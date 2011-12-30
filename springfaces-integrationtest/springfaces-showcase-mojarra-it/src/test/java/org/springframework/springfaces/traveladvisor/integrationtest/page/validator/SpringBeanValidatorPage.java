package org.springframework.springfaces.traveladvisor.integrationtest.page.validator;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/validator/springbean")
public class SpringBeanValidatorPage extends AbstractValidatorPageObject {

	public SpringBeanValidatorPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Validator - Spring");
	}
}

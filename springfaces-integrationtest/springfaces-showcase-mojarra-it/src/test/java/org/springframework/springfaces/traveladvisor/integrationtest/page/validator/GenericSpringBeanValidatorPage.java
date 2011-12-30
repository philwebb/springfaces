package org.springframework.springfaces.traveladvisor.integrationtest.page.validator;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/validator/genericspringbean")
public class GenericSpringBeanValidatorPage extends AbstractValidatorPageObject {

	public GenericSpringBeanValidatorPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Validator - Generic Spring");
	}
}

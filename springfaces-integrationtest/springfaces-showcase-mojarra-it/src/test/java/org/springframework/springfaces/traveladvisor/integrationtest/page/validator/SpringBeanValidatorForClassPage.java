package org.springframework.springfaces.traveladvisor.integrationtest.page.validator;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/validator/springbeanforclass")
public class SpringBeanValidatorForClassPage extends AbstractValidatorPageObject {

	public SpringBeanValidatorForClassPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Validator - Spring (forClass)");
	}
}

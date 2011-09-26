package org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/requestmapping/modelandview")
public class ModelAndViewRequestMappingPage extends PageObject {

	public ModelAndViewRequestMappingPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("RequestMapping - ModelAndView");
	}
}

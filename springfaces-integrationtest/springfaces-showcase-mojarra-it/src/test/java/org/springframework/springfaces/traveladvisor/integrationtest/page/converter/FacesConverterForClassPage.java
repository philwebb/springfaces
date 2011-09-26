package org.springframework.springfaces.traveladvisor.integrationtest.page.converter;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/converter/facesforclass?value=123")
public class FacesConverterForClassPage extends PageObject {

	public FacesConverterForClassPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Converter - Faces (forClass)");
	}

}

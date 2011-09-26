package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.FacesConverterByIdPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.FacesConverterForClassPage;

public class ConverterIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldSupportFacesConverterForClass() throws Exception {
		PageObject page = pages.get(FacesConverterForClassPage.class);
		assertThat(page.getBodyText(), is("Conversion for class : 123 from forClassFacesConverter"));
	}

	@Test
	public void shouldSupportFacesConverterByID() throws Exception {
		PageObject page = pages.get(FacesConverterByIdPage.class);
		assertThat(page.getBodyText(), is("Conversion by ID : 123 from byIdFacesConverter"));
	}

}

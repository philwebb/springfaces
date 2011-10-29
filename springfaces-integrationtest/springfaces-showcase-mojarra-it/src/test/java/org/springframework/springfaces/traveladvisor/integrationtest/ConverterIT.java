package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.FacesConverterByIdPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.FacesConverterForClassPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.GenericSpringConverterPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.SpringBeanForClassConverterPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.SpringConverterPage;
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

public class ConverterIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldSupportFacesConverterForClass() throws Exception {
		PageObject page = pages.get(FacesConverterForClassPage.class);
		assertThat(page.getBodyText(), is("Conversion for class : 123 from genericSpringBeanConverter"));
	}

	@Test
	public void shouldSupportFacesConverterByID() throws Exception {
		PageObject page = pages.get(FacesConverterByIdPage.class);
		assertThat(page.getBodyText(), is("Conversion by ID : 123 from byIdFacesConverter"));
	}

	@Test
	public void shouldSupportSpringBeanConverter() throws Exception {
		PageObject page = pages.get(SpringConverterPage.class);
		assertThat(page.getBodyText(), is("Conversion by ID : 123 from springBeanConverter"));
	}

	@Test
	public void shouldSupportGenericSpringBeanConverter() throws Exception {
		GenericSpringConverterPage page = pages.get(GenericSpringConverterPage.class);
		page.setInputText("123");
		page = page.clickSubmitButton();
		assertThat(page.getConversionText(), is("Conversion by ID : 123 from genericSpringBeanConverter"));
	}

	@Test
	public void shouldSupportSpringBeanForClass() throws Exception {
		PageObject page = pages.get(SpringBeanForClassConverterPage.class);
		assertThat(page.getBodyText(), is("Conversion by Class : 123 from forClassSpringConverter"));
	}
}

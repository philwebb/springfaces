package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.validator.AbstractValidatorPageObject;
import org.springframework.springfaces.traveladvisor.integrationtest.page.validator.GenericSpringBeanValidatorPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.validator.SpringBeanValidatorForClassPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.validator.SpringBeanValidatorPage;
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

/**
 * Integration tests for validators.
 * 
 * @author Phillip Web
 */
public class ValidatorIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldFailFromSpringValidator() throws Exception {
		AbstractValidatorPageObject page = pages.get(SpringBeanValidatorPage.class);
		page.setInputText("1");
		page = page.clickSubmitButton();
		assertThat(page.hasError(), is(true));
		assertThat(page.getErrorMessage(), is("Value must be 20 or more"));
	}

	@Test
	public void shouldPassFromSpringValidator() throws Exception {
		AbstractValidatorPageObject page = pages.get(SpringBeanValidatorPage.class);
		page.setInputText("20");
		page = page.clickSubmitButton();
		assertThat(page.hasError(), is(false));
	}

	@Test
	public void shouldFailFromGenericSpringValidator() throws Exception {
		AbstractValidatorPageObject page = pages.get(GenericSpringBeanValidatorPage.class);
		page.setInputText("1");
		page = page.clickSubmitButton();
		assertThat(page.hasError(), is(true));
		assertThat(page.getErrorMessage(), is("Value must be 10 or more"));
	}

	@Test
	public void shouldPassFromGenericSpringValidator() throws Exception {
		AbstractValidatorPageObject page = pages.get(GenericSpringBeanValidatorPage.class);
		page.setInputText("10");
		page = page.clickSubmitButton();
		assertThat(page.hasError(), is(false));
	}

	@Test
	public void shouldFailFromForClassValidator() throws Exception {
		AbstractValidatorPageObject page = pages.get(SpringBeanValidatorForClassPage.class);
		page.setInputText("1");
		page = page.clickSubmitButton();
		assertThat(page.hasError(), is(true));
		assertThat(page.getErrorMessage(), is("Value must be 30 or more"));
	}

	@Test
	public void shouldPassFromForClassValidator() throws Exception {
		AbstractValidatorPageObject page = pages.get(SpringBeanValidatorForClassPage.class);
		page.setInputText("30");
		page = page.clickSubmitButton();
		assertThat(page.hasError(), is(false));
	}
}

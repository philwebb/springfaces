package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping.ModelAndViewRequestMappingPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping.PostbackRequestMappingPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping.SimpleRequestMappingPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping.StringRequestMappingPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping.VariablesRequestMappingPage;

public class RequestMappingIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldGetSimpleRequestMapping() throws Exception {
		PageObject page = pages.get(SimpleRequestMappingPage.class);
		assertThat(page.getBodyText(), is("Simple @RequestMapping"));
	}

	@Test
	public void shouldGetStringRequestMapping() throws Exception {
		PageObject page = pages.get(StringRequestMappingPage.class);
		assertThat(page.getBodyText(), is("@RequestMapping Mapped By String Name"));
	}

	@Test
	public void shouldGetModelAndViewRequestMapping() throws Exception {
		PageObject page = pages.get(ModelAndViewRequestMappingPage.class);
		assertThat(page.getBodyText(), is("ModelAndView @RequestMapping"));
	}

	@Test
	public void shouldGetVariablesRequestMapping() throws Exception {
		String url = "/requestmapping/variables/example?argument=value";
		VariablesRequestMappingPage page = pages.get(VariablesRequestMappingPage.class, url);
		assertThat(page.getPathText(), is("path = example"));
		assertThat(page.getArgumentText(), is("argument = value"));
	}

	@Test
	public void shouldSupportPostbackWithRequestMapping() throws Exception {
		PostbackRequestMappingPage page = pages.get(PostbackRequestMappingPage.class);
		long date1 = page.getDate();
		Thread.sleep(500);
		page = page.clickPostbackButton();
		long date2 = page.getDate();
		assertTrue(date1 < date2);
	}
}

/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

/**
 * Integration tests for request mappings.
 * 
 * @author Phillip Web
 */
public class RequestMappingIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldGetSimpleRequestMapping() throws Exception {
		PageObject page = this.pages.get(SimpleRequestMappingPage.class);
		assertThat(page.getBodyText(), is("Simple @RequestMapping"));
	}

	@Test
	public void shouldGetStringRequestMapping() throws Exception {
		PageObject page = this.pages.get(StringRequestMappingPage.class);
		assertThat(page.getBodyText(), is("@RequestMapping Mapped By String Name"));
	}

	@Test
	public void shouldGetModelAndViewRequestMapping() throws Exception {
		PageObject page = this.pages.get(ModelAndViewRequestMappingPage.class);
		assertThat(page.getBodyText(), is("ModelAndView @RequestMapping"));
	}

	@Test
	public void shouldGetVariablesRequestMapping() throws Exception {
		String url = "/requestmapping/variables/example?argument=value";
		VariablesRequestMappingPage page = this.pages.get(VariablesRequestMappingPage.class, url);
		assertThat(page.getPathText(), is("path = example"));
		assertThat(page.getArgumentText(), is("argument = value"));
	}

	@Test
	public void shouldSupportPostbackWithRequestMapping() throws Exception {
		PostbackRequestMappingPage page = this.pages.get(PostbackRequestMappingPage.class);
		assertThat(page.getCreateDate(), is(not(0L)));
		long date1 = page.getDate();
		Thread.sleep(500);
		page = page.clickPostbackButton();
		assertThat(page.getCreateDate(), is(0L));
		long date2 = page.getDate();
		assertTrue(date1 < date2);
	}
}

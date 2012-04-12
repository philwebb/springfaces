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
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.FacesConverterByIdPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.FacesConverterForClassPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.GenericSpringBeanConverterPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.SpringBeanConverterPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.converter.SpringBeanForClassConverterPage;
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

/**
 * Integration tests for converters.
 * 
 * @author Phillip Web
 */
public class ConverterIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldSupportFacesConverterForClass() throws Exception {
		PageObject page = this.pages.get(FacesConverterForClassPage.class);
		assertThat(page.getBodyText(), is("Conversion for class : 123 from forClassFacesConverter"));
	}

	@Test
	public void shouldSupportFacesConverterByID() throws Exception {
		PageObject page = this.pages.get(FacesConverterByIdPage.class);
		assertThat(page.getBodyText(), is("Conversion by ID : 123 from byIdFacesConverter"));
	}

	@Test
	public void shouldSupportSpringBeanConverter() throws Exception {
		PageObject page = this.pages.get(SpringBeanConverterPage.class);
		assertThat(page.getBodyText(), is("Conversion by ID : 123 from springBeanConverter"));
	}

	@Test
	public void shouldSupportGenericSpringBeanConverter() throws Exception {
		GenericSpringBeanConverterPage page = this.pages.get(GenericSpringBeanConverterPage.class);
		page.setInputText("123");
		page = page.clickSubmitButton();
		assertThat(page.getConversionText(), is("Conversion by ID : 123 from genericSpringBeanConverter"));
	}

	@Test
	public void shouldSupportSpringBeanForClass() throws Exception {
		PageObject page = this.pages.get(SpringBeanForClassConverterPage.class);
		assertThat(page.getBodyText(), is("Conversion by Class : 123 from forClassSpringConverter"));
	}
}

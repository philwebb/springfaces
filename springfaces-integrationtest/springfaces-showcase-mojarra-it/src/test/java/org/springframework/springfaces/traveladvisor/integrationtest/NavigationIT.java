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

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateAnnotationHttpEntityPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateAnnotationLinkPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateAnnotationRerenderPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateAnnotationResponseBodyPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateAnnotationStreamingPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateAnnotationWithValuePage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateDirectPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateImplicitButtonPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateImplicitCommandButtonPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateImplicitCommandLinkPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateImplicitLinkPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateImplicitMvcRedirectPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateRuleButtonPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateRuleCommandButtonPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateRuleCommandLinkPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigateRuleLinkPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.navigate.NavigationDestinationPage;
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

/**
 * Integration tests for navigation.
 * 
 * @author Phillip Webb
 */
public class NavigationIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldNavigateImplicitLink() throws Exception {
		NavigateImplicitLinkPage page = pages.get(NavigateImplicitLinkPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from the implicit link"));
	}

	@Test
	public void shouldNavigateImplicitButton() throws Exception {
		NavigateImplicitButtonPage page = pages.get(NavigateImplicitButtonPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from the implicit button"));
	}

	@Test
	public void shouldNavigateImplicitCommandLink() throws Exception {
		NavigateImplicitCommandLinkPage page = pages.get(NavigateImplicitCommandLinkPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from the implicit command link"));
	}

	@Test
	public void shouldNavigateImplicitCommandButton() throws Exception {
		NavigateImplicitCommandButtonPage page = pages.get(NavigateImplicitCommandButtonPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from the implicit command button"));
	}

	@Test
	public void shouldNavigateImplicitMvcRedirect() throws Exception {
		NavigateImplicitMvcRedirectPage page = pages.get(NavigateImplicitMvcRedirectPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from implicit MVC redirect"));
	}

	@Test
	public void shouldNavigateRuleLink() throws Exception {
		NavigateRuleLinkPage page = pages.get(NavigateRuleLinkPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from the rule link"));
	}

	@Test
	public void shouldNavigateRuleButton() throws Exception {
		NavigateRuleButtonPage page = pages.get(NavigateRuleButtonPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from the rule button"));
	}

	@Test
	public void shouldNavigateRuleCommandLink() throws Exception {
		NavigateRuleCommandLinkPage page = pages.get(NavigateRuleCommandLinkPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from the rule command link"));
	}

	@Test
	public void shouldNavigateRuleCommandButton() throws Exception {
		NavigateRuleCommandButtonPage page = pages.get(NavigateRuleCommandButtonPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from the rule command button"));
	}

	@Test
	public void shouldNavigateDirect() throws Exception {
		NavigateDirectPage page = pages.get(NavigateDirectPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : direct"));
	}

	@Test
	public void shouldNavigateAnnotationLink() throws Exception {
		NavigateAnnotationLinkPage page = pages.get(NavigateAnnotationLinkPage.class);
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : from annotation"));
	}

	@Test
	public void shouldNavigateAnnotationWithValue() throws Exception {
		NavigateAnnotationWithValuePage page = pages.get(NavigateAnnotationWithValuePage.class);
		page.setInputText("/spring/navigation/destination?s=valuetest");
		NavigationDestinationPage destination = page.click();
		assertThat(destination.getBodyText(), is("Navigation Destination from : valuetest"));
	}

	@Test
	public void shouldNavigateAnnotationReRender() throws Exception {
		NavigateAnnotationRerenderPage page = pages.get(NavigateAnnotationRerenderPage.class);
		assertThat(page.getText(), is("null"));
		NavigateAnnotationRerenderPage destination = page.click();
		assertThat(destination.getText(), is(not("")));
	}

	@Test
	public void shouldNavigateAnnotationStreaming() throws Exception {
		NavigateAnnotationStreamingPage page = pages.get(NavigateAnnotationStreamingPage.class);
		assertThat(page.click(), is("hello"));
	}

	@Test
	public void shouldNavigateAnnotationResponseBody() throws Exception {
		NavigateAnnotationResponseBodyPage page = pages.get(NavigateAnnotationResponseBodyPage.class);
		assertThat(page.click(), is("responsebody"));
	}

	@Test
	public void shouldNavigateAnnotationHttpEntity() throws Exception {
		NavigateAnnotationHttpEntityPage page = pages.get(NavigateAnnotationHttpEntityPage.class);
		assertThat(page.click(), is("test"));
	}
}

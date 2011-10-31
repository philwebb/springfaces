package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
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
 * @author Phillip Web
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
		// FIXME
	}

	@Test
	public void shouldNavigateAnnotationWithValue() throws Exception {
		// FIXME
	}

	@Test
	public void shouldNavigateAnnotationReRender() throws Exception {
		// FIXME
	}

	@Test
	public void shouldNavigateAnnotationStreaming() throws Exception {
		// FIXME
	}

	@Test
	public void shouldNavigateAnnotationResponseBody() throws Exception {
		// FIXME
	}

	@Test
	public void shouldNavigateAnnotationHttpEntity() throws Exception {
		// FIXME
	}
}

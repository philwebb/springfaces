package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.message.DefinedSourceMessagePage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.message.MissingMessagePage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.message.ObjectsMessagePage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.message.ParametersMessagePage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.message.PrefixMessagePage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.message.SimpleMessagePage;
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

public class MessageIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldSupportSimpleMessages() throws Exception {
		PageObject page = pages.get(SimpleMessagePage.class);
		assertThat(page.getBodyText(), is("Simple Hello"));
	}

	@Test
	public void shouldSupportSpecifiedPrefix() throws Exception {
		PageObject page = pages.get(PrefixMessagePage.class);
		assertThat(page.getBodyText(), is("Prefix Hello"));
	}

	@Test
	public void shouldSupportDefinedSource() throws Exception {
		PageObject page = pages.get(DefinedSourceMessagePage.class);
		assertThat(page.getBodyText(), is("Defined Source Hello"));
	}

	@Test
	public void shouldSupportParameters() throws Exception {
		PageObject page = pages.get(ParametersMessagePage.class);
		assertThat(page.getBodyText(), is("Parameters Hello from San Francisco in California"));
	}

	@Test
	public void shouldSupportObjects() throws Exception {
		PageObject page = pages.get(ObjectsMessagePage.class);
		assertThat(page.getBodyText(), is("San Francisco (California)"));
	}

	@Test
	public void shouldSupportMissingMessage() throws Exception {
		MissingMessagePage page = pages.get(MissingMessagePage.class);
		assertThat(page.getOutputText(), is("idontexist"));
		assertThat(page.getErrorMessage(),
				startsWith("No message found under code 'pages.message.missing.idontexist' for locale "));
	}
}

package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.template.ComponentInfoPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.template.DecorateAllPage;
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

public class TemplateIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldHaveComponentInfo() throws Exception {
		ComponentInfoPage page = this.pages.get(ComponentInfoPage.class);
		assertThat(page.getDetails(), is("Given Name form:name true true name false"));
		page = page.clickSubmitButton();
		assertThat(page.getDetails(), is("Given Name form:name false true name false"));
	}

	@Test
	public void shouldDecorateAll() throws Exception {
		DecorateAllPage page = this.pages.get(DecorateAllPage.class);
		StringBuffer expected = new StringBuffer();
		expected.append("beforeinsert1after");
		expected.append("globaldefinevalue");
		expected.append("globalparamvalue");
		expected.append("localdefinevalue1");
		expected.append("localparamvalue1");
		expected.append("beforeinsert1after");
		expected.append("globaldefinevalue");
		expected.append("globalparamvalue");
		expected.append("localdefinevalue2");
		expected.append("localparamvalue2");
		assertThat(page.getBodyText().replaceAll("\n", ""), is(expected.toString()));
	}
}

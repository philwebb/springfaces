package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems.SelectManyEnumSelectItemsPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems.SelectManyStringSelectItemsPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems.SelectOneBooleanSelectItemsPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems.SelectOneJpaSelectItemsPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems.SelectOption;
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

/**
 * Integration tests for select items.
 * 
 * @author Phillip Web
 */
public class SelectItemsIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldSelectOneJpa() throws Exception {
		SelectOneJpaSelectItemsPage page = pages.get(SelectOneJpaSelectItemsPage.class);
		List<SelectOption> selectOptions = page.getSelectOptions();
		assertThat(selectOptions.size(), is(28));
		assertThat(selectOptions.get(0).getValue(), is(""));
		assertThat(selectOptions.get(0).getSelected(), is("true"));
		assertThat(selectOptions.get(0).getText(), is("--- Please Select ---"));
		assertThat(selectOptions.get(1).getValue(), is("0"));
		assertThat(selectOptions.get(1).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(1).getText(), is("Rod Johnson"));

		page.selectSecondOption();

		page = (SelectOneJpaSelectItemsPage) page.clickSubmitButton();

		selectOptions = page.getSelectOptions();
		assertThat(selectOptions.size(), is(28));
		assertThat(selectOptions.get(0).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(1).getSelected(), is("true"));
	}

	@Test
	public void shouldSelectManyString() throws Exception {
		SelectManyStringSelectItemsPage page = pages.get(SelectManyStringSelectItemsPage.class);
		List<SelectOption> selectOptions = page.getSelectOptions();
		assertThat(selectOptions.size(), is(4));
		assertThat(selectOptions.get(0).getValue(), is("Framework"));
		assertThat(selectOptions.get(0).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(0).getText(), is("Framework"));
		assertThat(selectOptions.get(1).getValue(), is("Web Flow"));
		assertThat(selectOptions.get(1).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(1).getText(), is("Web Flow"));

		page.selectSecondOption();

		page = (SelectManyStringSelectItemsPage) page.clickSubmitButton();

		selectOptions = page.getSelectOptions();
		assertThat(selectOptions.size(), is(4));
		assertThat(selectOptions.get(0).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(1).getSelected(), is("true"));
	}

	@Test
	public void shouldSelectOneBoolean() throws Exception {
		SelectOneBooleanSelectItemsPage page = pages.get(SelectOneBooleanSelectItemsPage.class);
		List<SelectOption> selectOptions = page.getSelectOptions();
		assertThat(selectOptions.size(), is(3));
		assertThat(selectOptions.get(0).getValue(), is(""));
		assertThat(selectOptions.get(0).getSelected(), is("true"));
		assertThat(selectOptions.get(0).getText(), is("--- Please Select ---"));
		assertThat(selectOptions.get(1).getValue(), is("true"));
		assertThat(selectOptions.get(1).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(1).getText(), is("Yes"));
		assertThat(selectOptions.get(2).getValue(), is("false"));
		assertThat(selectOptions.get(2).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(2).getText(), is("No"));

		page.selectSecondOption();

		page = (SelectOneBooleanSelectItemsPage) page.clickSubmitButton();

		selectOptions = page.getSelectOptions();
		assertThat(selectOptions.size(), is(3));
		assertThat(selectOptions.get(0).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(1).getSelected(), is("true"));
	}

	@Test
	public void shouldSelectManyEnum() throws Exception {
		SelectManyEnumSelectItemsPage page = pages.get(SelectManyEnumSelectItemsPage.class);
		List<SelectOption> selectOptions = page.getSelectOptions();
		assertThat(selectOptions.size(), is(3));
		assertThat(selectOptions.get(0).getValue(), is("JAVA"));
		assertThat(selectOptions.get(0).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(0).getText(), is("Java"));
		assertThat(selectOptions.get(1).getValue(), is("SPRING"));
		assertThat(selectOptions.get(1).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(1).getText(), is("Spring"));
		assertThat(selectOptions.get(2).getValue(), is("JAVASERVER_FACES"));
		assertThat(selectOptions.get(2).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(2).getText(), is("JavaServer Faces"));
		page.selectSecondOption();

		page = (SelectManyEnumSelectItemsPage) page.clickSubmitButton();

		selectOptions = page.getSelectOptions();
		assertThat(selectOptions.size(), is(3));
		assertThat(selectOptions.get(0).getSelected(), is(nullValue()));
		assertThat(selectOptions.get(1).getSelected(), is("true"));

	}

}

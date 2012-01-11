package org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/selectitems/selectonejpa")
public class SelectOneJpaSelectItemsPage extends AbstractMenuSelectItemsPageObject {

	public SelectOneJpaSelectItemsPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Select Items - Select One (JPA)");
	}
}

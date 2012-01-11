package org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/selectitems/selectoneboolean")
public class SelectOneBooleanSelectItemsPage extends AbstractMenuSelectItemsPageObject {

	public SelectOneBooleanSelectItemsPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Select Items - Select One (Boolean)");
	}
}

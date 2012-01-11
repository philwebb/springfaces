package org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/selectitems/selectmanystring")
public class SelectManyStringSelectItemsPage extends AbstractMenuSelectItemsPageObject {

	public SelectManyStringSelectItemsPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Select Items - Select Many (String)");
	}

}

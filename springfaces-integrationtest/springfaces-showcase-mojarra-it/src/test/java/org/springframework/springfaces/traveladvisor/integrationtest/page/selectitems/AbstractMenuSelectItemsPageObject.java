package org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public abstract class AbstractMenuSelectItemsPageObject extends AbstractSelectItemsPageObject {

	public AbstractMenuSelectItemsPageObject(WebDriver webDriver) {
		super(webDriver);
	}

	public List<SelectOption> getSelectOptions() {
		List<SelectOption> selectOptions = new ArrayList<SelectOption>();
		WebElement selectElement = getWebDriver().findElement(By.id("form:select"));
		List<WebElement> options = selectElement.findElements(By.xpath("option"));
		for (WebElement option : options) {
			String value = option.getAttribute("value");
			String selected = option.getAttribute("selected");
			String text = option.getText();
			selectOptions.add(new SelectOption(value, selected, text));
		}
		return selectOptions;
	}

	public void selectSecondOption() {
		WebElement selectElement = getWebDriver().findElement(By.id("form:select"));
		Select select = new Select(selectElement);
		select.selectByIndex(1);
	}

}

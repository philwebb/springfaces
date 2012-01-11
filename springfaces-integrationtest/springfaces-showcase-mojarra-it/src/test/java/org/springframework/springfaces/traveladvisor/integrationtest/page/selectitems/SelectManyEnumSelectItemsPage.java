package org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;

@PageURL("/selectitems/selectmanyenum")
public class SelectManyEnumSelectItemsPage extends AbstractSelectItemsPageObject {

	public SelectManyEnumSelectItemsPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("Select Items - Select Many (Enum)");
	}

	public List<SelectOption> getSelectOptions() {
		List<SelectOption> selectOptions = new ArrayList<SelectOption>();
		WebElement tableElement = getWebDriver().findElement(By.id("form:select"));
		List<WebElement> tableDataElements = tableElement.findElements(By.xpath("tbody/tr/td"));
		for (WebElement tableDataElement : tableDataElements) {
			WebElement inputElement = tableDataElement.findElement(By.tagName("input"));
			String value = inputElement.getAttribute("value");
			String selected = inputElement.getAttribute("checked");
			String text = tableDataElement.getText();
			selectOptions.add(new SelectOption(value, selected, text));
		}
		return selectOptions;
	}

	public void selectSecondOption() {
		WebElement checkBox = getWebDriver().findElement(By.id("form:select:1"));
		checkBox.click();
	}

}

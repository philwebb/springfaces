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

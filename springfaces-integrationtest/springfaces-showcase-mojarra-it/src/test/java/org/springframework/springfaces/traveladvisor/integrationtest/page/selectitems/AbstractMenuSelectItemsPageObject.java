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

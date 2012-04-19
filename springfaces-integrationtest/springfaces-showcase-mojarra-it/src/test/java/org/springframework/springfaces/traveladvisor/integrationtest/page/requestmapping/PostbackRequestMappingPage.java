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
package org.springframework.springfaces.traveladvisor.integrationtest.page.requestmapping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;
import org.springframework.springfaces.integrationtest.selenium.page.PageURL;
import org.springframework.util.StringUtils;

@PageURL("/requestmapping/postback")
public class PostbackRequestMappingPage extends PageObject {

	public PostbackRequestMappingPage(WebDriver webDriver) {
		super(webDriver);
	}

	@Override
	protected boolean isCorrectPage(String title) {
		return title.equals("RequestMapping - Postback");
	}

	public long getCreateDate() {
		return getDate("createText");
	}

	public long getDate() {
		return getDate("dateText");
	}

	private long getDate(String id) {
		String dateText = getWebDriver().findElement(By.id(id)).getText();
		if (StringUtils.hasLength(dateText)) {
			return Long.valueOf(dateText);
		}
		return 0L;
	}

	public PostbackRequestMappingPage clickPostbackButton() {
		getWebDriver().findElement(By.id("form:postback")).click();
		return newPage(PostbackRequestMappingPage.class);
	}

}

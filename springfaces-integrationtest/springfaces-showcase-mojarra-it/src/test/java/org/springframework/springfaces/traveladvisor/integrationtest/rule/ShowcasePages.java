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
package org.springframework.springfaces.traveladvisor.integrationtest.rule;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.springfaces.integrationtest.selenium.PooledWebDriverManager;
import org.springframework.springfaces.integrationtest.selenium.WebDriverFactory;
import org.springframework.springfaces.integrationtest.selenium.WebDriverManager;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;

public class ShowcasePages extends Pages {

	private static final WebDriverFactory FACTORY = new WebDriverFactory() {
		public WebDriver newWebDriver() {
			try {
				FirefoxDriver webDriver = new FirefoxDriver();
				webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				return webDriver;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		}
	};

	private static final WebDriverManager MANAGER = new PooledWebDriverManager(FACTORY);

	public ShowcasePages() {
	}

	@Override
	protected String getRootUrl() {
		return "http://localhost:8080/springfaces-showcase/spring";
	}

	@Override
	protected WebDriverManager getWebDriverManager() {
		return MANAGER;
	}
}

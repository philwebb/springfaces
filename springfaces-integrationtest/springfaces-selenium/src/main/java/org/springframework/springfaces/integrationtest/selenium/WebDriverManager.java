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
package org.springframework.springfaces.integrationtest.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Interface that can be used to manage {@link WebDriver}s.
 * 
 * @author Phillip Webb
 */
public interface WebDriverManager {

	/**
	 * Returns a new web driver. When the driver is no longer required the {@link #releaseWebDriver(WebDriver)} method
	 * should be called.
	 * @return a web driver instance
	 * @see #releaseWebDriver(WebDriver)
	 */
	WebDriver getWebDriver();

	/**
	 * Release a previously {@link #getWebDriver() obtained} web driver instance.
	 * @param webDriver the instance to release
	 * @see #getWebDriver()
	 */
	void releaseWebDriver(WebDriver webDriver);
}

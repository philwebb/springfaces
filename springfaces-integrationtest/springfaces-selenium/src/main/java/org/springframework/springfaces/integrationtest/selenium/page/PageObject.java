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
package org.springframework.springfaces.integrationtest.selenium.page;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.springfaces.integrationtest.selenium.WebDriverUtils;
import org.springframework.util.Assert;

/**
 * Base class to help implement that <tt>Page Object</tt> Design Pattern as recommended by Selenium.
 * 
 * @author Phillip Webb
 */
public abstract class PageObject implements Page {

	private WebDriver webDriver;

	/**
	 * Create a new {@link PageObject} instance.
	 * @param webDriver the web driver
	 */
	public PageObject(WebDriver webDriver) {
		Assert.notNull(webDriver, "WebDriver must not be null");
		this.webDriver = webDriver;
		String title = webDriver.getTitle();
		Assert.isTrue(isCorrectPage(title), "Incorrect page ('" + title + "') loaded");
	}

	/**
	 * Called to verify that the correct page has been loaded. For convenience the page title is provided, other
	 * attributes from the {@link #getWebDriver() WebDriver} can also be used to verify that the correct page has
	 * loaded.
	 * @param title the title of the current page.
	 * @return <tt>true</tt> if the correct page has loaded
	 */
	protected abstract boolean isCorrectPage(String title);

	/**
	 * Returns the underlying web driver.
	 * @return the web driver
	 */
	protected final WebDriver getWebDriver() {
		return webDriver;
	}

	/**
	 * Find a {@link WebElement} by ID.
	 * @param id the ID to find
	 * @return the {@link WebElement}.
	 * @throws NoSuchElementException If no matching elements are found
	 */
	protected final WebElement findElementById(String id) {
		Assert.notNull(id, "ID must not be null");
		return getWebDriver().findElement(By.id(id));
	}

	/**
	 * Decorate the specified {@link WebElement} with a version that will {@link #waitOnUrlChange(String) wait for a URL
	 * change} after each call. Particularly useful when dealing with AJAX driven redirect.
	 * <p>
	 * For example: <code>
	 * waitOnUrlChange(searchButton).click();
	 * </code>
	 * @param source the source element
	 * @return a decorated {@link WebElement}
	 * @see #waitOnUrlChange(String)
	 * @see WebDriverUtils#waitOnUrlChange(WebDriver, WebElement)
	 */
	protected final WebElement waitOnUrlChange(final WebElement source) {
		return WebDriverUtils.waitOnUrlChange(getWebDriver(), source);
	}

	/**
	 * Wait until the current URL changes from the specified URL.
	 * @param fromUrl the current URL that must change before this method continues
	 * @see #waitOnUrlChange(WebElement)
	 * @see WebDriverUtils#waitOnUrlChange(WebDriver, String)
	 */
	protected final void waitOnUrlChange(final String fromUrl) {
		WebDriverUtils.waitOnUrlChange(getWebDriver(), fromUrl);
	}

	/**
	 * Create a new {@link PageObject} instance of the specified class.
	 * @param pageClass the page object class
	 * @return a new page object
	 */
	protected final <P extends PageObject> P newPage(Class<P> pageClass) {
		return WebDriverUtils.newPage(getWebDriver(), pageClass);
	}

	/**
	 * Returns the body text of the page.
	 * @return the body text
	 */
	public String getBodyText() {
		return getWebDriver().findElement(By.tagName("body")).getText();
	}
}

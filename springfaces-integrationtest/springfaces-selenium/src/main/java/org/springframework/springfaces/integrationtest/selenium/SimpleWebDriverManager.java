package org.springframework.springfaces.integrationtest.selenium;

import org.openqa.selenium.WebDriver;
import org.springframework.util.Assert;

/**
 * Simple {@link WebDriverManager} that create new connections each time.
 * 
 * @author Phillip Webb
 */
public class SimpleWebDriverManager implements WebDriverManager {

	private WebDriverFactory factory;

	public SimpleWebDriverManager(WebDriverFactory factory) {
		Assert.notNull(factory, "Factory must not be null");
		this.factory = factory;
	}

	public WebDriver getWebDriver() {
		return factory.newWebDriver();
	}

	public void releaseWebDriver(WebDriver webDriver) {
		webDriver.close();
	}
}

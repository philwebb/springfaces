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

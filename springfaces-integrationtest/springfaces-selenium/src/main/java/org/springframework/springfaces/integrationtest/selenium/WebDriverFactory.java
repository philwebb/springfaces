package org.springframework.springfaces.integrationtest.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Factory that can be used to create a new {@link WebDriver} instance.
 * 
 * @author Phillip Webb
 */
public interface WebDriverFactory {

	/**
	 * Create a new {@link WebDriver} instance.
	 * @return a new instance
	 */
	public WebDriver newWebDriver();
}

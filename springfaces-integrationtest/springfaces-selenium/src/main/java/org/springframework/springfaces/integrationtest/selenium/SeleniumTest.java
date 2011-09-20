package org.springframework.springfaces.integrationtest.selenium;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Base class for selenium tests.
 * 
 * @author Phillip Webb
 */
public abstract class SeleniumTest {

	//FIXME replace with a runner
	
	private WebDriver webDriver;

	@Before
	public void setupWebDriver() {
		this.webDriver = new FirefoxDriver();
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	@After
	public void closeWebDriver() {
		this.webDriver.close();
	}
	
	public WebDriver webDriver() {
		return webDriver;
	}
}

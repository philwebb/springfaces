package org.springframework.springfaces.traveladvisor.integrationtest;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.springfaces.integrationtest.selenium.WebDriverFactory;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;

public class TravelAdvisorPages extends Pages {

	private static final WebDriverFactory FIREFOX_WEBDRIVER_FACTORY = new WebDriverFactory() {
		public WebDriver newWebDriver() {
			FirefoxDriver webDriver = new FirefoxDriver();
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			return webDriver;
		}
	};

	@Override
	protected String getRootUrl() {
		return "http://localhost:8080/springfaces-traveladvisor";
	}

	@Override
	protected WebDriverFactory getWebDriverFactory() {
		return FIREFOX_WEBDRIVER_FACTORY;
	}
}

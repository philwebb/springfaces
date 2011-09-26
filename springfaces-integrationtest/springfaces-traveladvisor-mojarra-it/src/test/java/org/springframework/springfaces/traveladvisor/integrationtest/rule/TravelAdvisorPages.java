package org.springframework.springfaces.traveladvisor.integrationtest.rule;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.springfaces.integrationtest.selenium.PooledWebDriverManager;
import org.springframework.springfaces.integrationtest.selenium.WebDriverFactory;
import org.springframework.springfaces.integrationtest.selenium.WebDriverManager;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;

public class TravelAdvisorPages extends Pages {

	private static final WebDriverFactory FACTORY = new WebDriverFactory() {
		public WebDriver newWebDriver() {
			FirefoxDriver webDriver = new FirefoxDriver();
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			return webDriver;
		}
	};

	private static final WebDriverManager MANAGER = new PooledWebDriverManager(FACTORY);

	@Override
	protected String getRootUrl() {
		return "http://localhost:8080/springfaces-traveladvisor";
	}

	@Override
	protected WebDriverManager getWebDriverManager() {
		return MANAGER;
	}
}

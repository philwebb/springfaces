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

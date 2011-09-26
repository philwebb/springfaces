package org.springframework.springfaces.integrationtest.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.springframework.util.Assert;

/**
 * A Pooled {@link WebDriverManager} that reuses web driver connections. A {@link Runtime} release hook ensures all
 * drivers are closed when the JVM exits.
 * 
 * @author Phillip Webb
 */
public class PooledWebDriverManager implements WebDriverManager {

	private WebDriverFactory factory;

	private List<WebDriver> pool = new ArrayList<WebDriver>();

	public PooledWebDriverManager(WebDriverFactory factory) {
		Assert.notNull(factory, "Factory must not be null");
		this.factory = factory;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				fullyReleasePool();
			}
		});
	}

	public WebDriver getWebDriver() {
		synchronized (pool) {
			if (!pool.isEmpty()) {
				return pool.remove(0);
			}
		}
		return factory.newWebDriver();
	}

	public void releaseWebDriver(WebDriver webDriver) {
		synchronized (pool) {
			pool.add(webDriver);
		}
	}

	protected void fullyReleasePool() {
		synchronized (pool) {
			for (WebDriver webDriver : pool) {
				webDriver.close();
			}
		}
	}
}

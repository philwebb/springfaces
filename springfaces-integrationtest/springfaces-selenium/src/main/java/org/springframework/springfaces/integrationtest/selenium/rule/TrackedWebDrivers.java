package org.springframework.springfaces.integrationtest.selenium.rule;

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.WebDriverFactory;
import org.springframework.util.Assert;

/**
 * A JUnit {@link MethodRule} that can be used to create {@link WebDriver}s that are automatically
 * {@link WebDriver#close() closed} when the test method has completed.
 * 
 * @author Phillip Webb
 */
public class TrackedWebDrivers implements MethodRule {

	private static ThreadLocal<WebDriverTrackingStatement> activeStatement = new ThreadLocal<WebDriverTrackingStatement>();

	private WebDriverFactory webDriverFactory;

	/**
	 * Create a new {@link TrackedWebDrivers} instance. This constructor can be used by subclasses that implement
	 * {@link #getWebDriverFactory()}.
	 */
	protected TrackedWebDrivers() {
	}

	/**
	 * Create a new {@link TrackedWebDrivers} instance.
	 * @param webDriverFactory the web driver factory used to obtain {@link WebDriver}s
	 */
	public TrackedWebDrivers(WebDriverFactory webDriverFactory) {
		Assert.notNull(webDriverFactory, "WebDriverFactory must not be null");
		this.webDriverFactory = webDriverFactory;
	}

	/**
	 * Create a new {@link WebDriver} that will automatically be closed when the current test method has completed. This
	 * method can only be called from an active test method.
	 * @return a new {@link WebDriver} instance.
	 */
	public WebDriver newWebDriver() {
		WebDriverTrackingStatement statement = activeStatement.get();
		Assert.state(statement != null, "No active JUnit statement found, is an appropriate @Rule configured");
		WebDriver webDriver = getWebDriverFactory().newWebDriver();
		statement.track(webDriver);
		return webDriver;
	}

	/**
	 * Returns the {@link WebDriverFactory} used to obtain the {@link WebDriver}.
	 * @return the factory
	 */
	protected WebDriverFactory getWebDriverFactory() {
		return webDriverFactory;
	}

	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		return new WebDriverTrackingStatement(base);
	}

	private class WebDriverTrackingStatement extends Statement {

		private List<WebDriver> tracked = new ArrayList<WebDriver>();

		private Statement base;

		public WebDriverTrackingStatement(Statement base) {
			this.base = base;
		}

		@Override
		public void evaluate() throws Throwable {
			activeStatement.set(this);
			try {
				base.evaluate();
			} finally {
				activeStatement.set(null);
				cleanupTrackedResources();
			}
		}

		void track(WebDriver webDriver) {
			tracked.add(webDriver);
		}

		private void cleanupTrackedResources() {
			for (WebDriver webDriver : tracked) {
				webDriver.close();
			}
		}
	}
}

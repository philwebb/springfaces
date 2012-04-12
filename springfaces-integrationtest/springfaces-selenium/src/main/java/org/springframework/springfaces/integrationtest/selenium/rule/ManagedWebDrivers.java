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
package org.springframework.springfaces.integrationtest.selenium.rule;

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.WebDriverFactory;
import org.springframework.springfaces.integrationtest.selenium.WebDriverManager;
import org.springframework.util.Assert;

/**
 * A JUnit {@link MethodRule} that can be used to create {@link WebDriver}s that are automatically
 * {@link WebDriver#close() closed} when the test method has completed.
 * 
 * @author Phillip Webb
 */
public class ManagedWebDrivers implements MethodRule {

	private static ThreadLocal<WebDriverTrackingStatement> activeStatement = new ThreadLocal<WebDriverTrackingStatement>();

	private WebDriverManager webDriverManager;

	/**
	 * Create a new {@link ManagedWebDrivers} instance. This constructor can be used by subclasses that implement
	 * {@link #getWebDriverManager()}.
	 */
	protected ManagedWebDrivers() {
	}

	/**
	 * Create a new {@link ManagedWebDrivers} instance.
	 * @param webDriverManager the web driver manager used to obtain and release {@link WebDriver}s
	 */
	public ManagedWebDrivers(WebDriverManager webDriverManager) {
		Assert.notNull(webDriverManager, "WebDriverManager must not be null");
		this.webDriverManager = webDriverManager;
	}

	/**
	 * Create a new {@link WebDriver} that will automatically be closed when the current test method has completed. This
	 * method can only be called from an active test method.
	 * @return a new {@link WebDriver} instance.
	 */
	public WebDriver getWebDriver() {
		WebDriverTrackingStatement statement = activeStatement.get();
		Assert.state(statement != null, "No active JUnit statement found, is an appropriate @Rule configured");
		WebDriver webDriver = getWebDriverManager().getWebDriver();
		statement.track(webDriver);
		return webDriver;
	}

	protected void releaseWebDriver(WebDriver webDriver) {
		getWebDriverManager().releaseWebDriver(webDriver);
	}

	/**
	 * Returns the {@link WebDriverFactory} used to obtain the {@link WebDriver}.
	 * @return the factory
	 */
	protected WebDriverManager getWebDriverManager() {
		return webDriverManager;
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
				releaseWebDriver(webDriver);
			}
		}
	}
}

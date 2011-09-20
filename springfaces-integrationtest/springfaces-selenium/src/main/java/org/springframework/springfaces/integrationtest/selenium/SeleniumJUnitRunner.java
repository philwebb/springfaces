package org.springframework.springfaces.integrationtest.selenium;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * A JUnit 4 {@link Runner} to support Selenium web tests. This runner will setup and tear down a {@link WebDriver} and
 * inject any {@link Page} annotated fields.
 * 
 * @author Phillip Webb
 */
public class SeleniumJUnitRunner extends BlockJUnit4ClassRunner {

	public SeleniumJUnitRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		Statement statement = super.methodInvoker(method, test);
		statement = withPages(method, test, statement);
		return statement;
	}

	private Statement withPages(FrameworkMethod method, Object test, Statement statement) {
		List<FrameworkField> annotatedFields = getTestClass().getAnnotatedFields(Page.class);
		for (FrameworkField field : annotatedFields) {
			statement = new InjectWebDriverStatememnt(test, statement, field.getField());
		}
		return statement;
	}

	private class InjectWebDriverStatememnt extends Statement {

		private Object test;
		private Statement statement;
		private Field field;

		public InjectWebDriverStatememnt(Object test, Statement statement, Field field) {
			this.test = test;
			this.statement = statement;
			this.field = field;
		}

		@Override
		public void evaluate() throws Throwable {
			WebDriver webDriver = createWebDriver();
			try {
				setupField(webDriver);
				try {
					statement.evaluate();
				} finally {
					tearDownField();
				}
			} finally {
				webDriver.close();
			}
		}

		private WebDriver createWebDriver() {
			FirefoxDriver webDriver = new FirefoxDriver();
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			String url = field.getAnnotation(Page.class).value();
			webDriver.get(url);
			return webDriver;
		}

		private void setupField(WebDriver webDriver) throws Exception {
			field.setAccessible(true);
			Class<?> type = field.getType();
			Object value = type.getConstructor(WebDriver.class).newInstance(webDriver);
			field.set(test, value);
		}

		private void tearDownField() throws Exception {
			field.set(test, null);
		}
	}

}

package org.springframework.springfaces.integrationtest.selenium.page;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import com.thoughtworks.selenium.Wait;

/**
 * Base class to help implement that "Page Object Design Pattern" as recommended by Selenium.
 * 
 * @author Phillip Webb
 */
public abstract class PageObject {

	private WebDriver webDriver;

	/**
	 * Create a new {@link PageObject} instance.
	 * @param webDriver the web driver
	 */
	public PageObject(WebDriver webDriver) {
		Assert.notNull(webDriver, "WebDriver must not be null");
		this.webDriver = webDriver;
		String title = webDriver.getTitle();
		Assert.isTrue(isCorrectPage(title), "Incorrect page ('" + title + "') loaded");
	}

	/**
	 * Called to verify that the correct page has been loaded. For convenience the page title is provided, other
	 * attributes from the {@link #getWebDriver() WebDriver} can also be used to verify that the correct page has
	 * loaded.
	 * @param title the title of the current page.
	 * @return <tt>true</tt> if the correct page has loaded
	 */
	protected abstract boolean isCorrectPage(String title);

	/**
	 * Returns the underlying web driver.
	 * @return the web driver
	 */
	protected final WebDriver getWebDriver() {
		return webDriver;
	}

	/**
	 * Find a {@link WebElement} by ID.
	 * @param id the ID to find
	 * @return the {@link WebElement}.
	 * @throws NoSuchElementException If no matching elements are found
	 */
	protected final WebElement findElementById(String id) {
		Assert.notNull(id, "ID must not be null");
		return getWebDriver().findElement(By.id(id));
	}

	/**
	 * Decorate the specified {@link WebElement} with a version that will {@link #waitOnUrlChange(String) wait for a URL
	 * change} after each call. Particularly useful when dealing with AJAX driven redirect.
	 * <p>
	 * For example: <code>
	 * waitOnUrlChange(searchButton).click();
	 * </code>
	 * @param source the source element
	 * @return a decorated {@link WebElement}
	 * @see #waitOnUrlChange(String)
	 */
	protected final WebElement waitOnUrlChange(final WebElement source) {
		final String url = getWebDriver().getCurrentUrl();
		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { WebElement.class },
				new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						try {
							return method.invoke(source, args);
						} finally {
							waitOnUrlChange(url);
						}
					}
				});
		return (WebElement) proxy;
	}

	/**
	 * Wait until the current URL changes from the specified URL.
	 * @param fromUrl the current URL that must change before this method continues
	 * @see #waitOnUrlChange(WebElement)
	 */
	protected final void waitOnUrlChange(final String fromUrl) {
		new Wait("Expected change of URL from " + fromUrl) {
			@Override
			public boolean until() {
				return !getWebDriver().getCurrentUrl().equals(fromUrl);
			}
		};
	}

	/**
	 * Create a new {@link PageObject} instance of the specified class.
	 * @param pageObjectClass the page object class
	 * @return a new page object
	 */
	protected final <T extends PageObject> T newPage(Class<T> pageObjectClass) {
		try {
			return pageObjectClass.getConstructor(WebDriver.class).newInstance(getWebDriver());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}

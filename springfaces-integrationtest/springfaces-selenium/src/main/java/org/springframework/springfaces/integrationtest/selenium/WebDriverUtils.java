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
package org.springframework.springfaces.integrationtest.selenium;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.BeanUtils;
import org.springframework.springfaces.integrationtest.selenium.page.Page;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.thoughtworks.selenium.Wait;

/**
 * Static convenience methods for {@link WebDriver}.
 * 
 * @author Phillip Webb
 */
public abstract class WebDriverUtils {

	/**
	 * Decorate the specified {@link WebElement} with a version that will {@link #waitOnUrlChange(WebDriver, String)
	 * wait for a URL change} after each call. Particularly useful when dealing with AJAX driven redirect.
	 * <p>
	 * For example: <code>
	 * waitOnUrlChange(searchButton).click();
	 * </code>
	 * @param webDriver the web driver
	 * @param source the source element
	 * @return a decorated {@link WebElement}
	 * @see #waitOnUrlChange(WebDriver,String)
	 */
	public static WebElement waitOnUrlChange(final WebDriver webDriver, final WebElement source) {
		Assert.notNull(webDriver, "WebDriver must not be null");
		Assert.notNull(source, "Source must not be null");
		final String url = webDriver.getCurrentUrl();
		Object proxy = Proxy.newProxyInstance(source.getClass().getClassLoader(), new Class<?>[] { WebElement.class },
				new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						try {
							return method.invoke(source, args);
						} finally {
							waitOnUrlChange(webDriver, url);
						}
					}
				});
		return (WebElement) proxy;
	}

	/**
	 * Wait until the current URL changes from the specified URL.
	 * @param webDriver the web driver
	 * @param fromUrl the current URL that must change before this method continues
	 * @see #waitOnUrlChange(WebDriver,WebElement)
	 */
	public static void waitOnUrlChange(final WebDriver webDriver, final String fromUrl) {
		Assert.notNull(webDriver, "WebDriver must not be null");
		Assert.notNull(fromUrl, "FromUrl must not be null");
		new Wait("Expected change of URL from " + fromUrl) {
			@Override
			public boolean until() {
				return !webDriver.getCurrentUrl().equals(fromUrl);
			}
		};
	}

	/**
	 * Create a new page of the specified type.
	 * @param webDriver The web driver (at the appropriate URL)
	 * @param pageClass the page class to create
	 * @return the page
	 */
	public static <P extends Page> P newPage(WebDriver webDriver, Class<P> pageClass) {
		Assert.notNull(webDriver, "WebDriver must not be null");
		Assert.notNull(pageClass, "PageClass must not be null");
		Constructor<P> constructor = ClassUtils.getConstructorIfAvailable(pageClass, WebDriver.class);
		Assert.state(constructor != null, "The page class " + pageClass.getName()
				+ " must include a public constructor with a single WebDriver argument");
		return BeanUtils.instantiateClass(constructor, webDriver);
	}

}

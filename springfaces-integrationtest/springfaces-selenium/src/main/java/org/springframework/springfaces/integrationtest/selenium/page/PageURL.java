package org.springframework.springfaces.integrationtest.selenium.page;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.springfaces.integrationtest.selenium.rule.Pages;

/**
 * Annotation used to mark the URL of a {@link Page}.
 * 
 * @see Pages
 * @author Phillip Webb
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PageURL {
	/**
	 * Returns the URL of the page
	 * @return the URL
	 */
	String value();
}

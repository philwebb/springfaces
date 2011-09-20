package org.springframework.springfaces.integrationtest.selenium;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.WebDriver;
import org.springframework.springfaces.integrationtest.selenium.page.PageObject;

/**
 * Filed annotation to mark a page object that should be setup by the {@link SeleniumJUnitRunner}. The field type must
 * have a constructor that accepts a single {@link WebDriver} argument.
 * 
 * @see PageObject
 * @author Phillip Webb
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Page {
	String value();
}

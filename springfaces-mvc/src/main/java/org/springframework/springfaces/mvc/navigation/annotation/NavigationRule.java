package org.springframework.springfaces.mvc.navigation.annotation;


public @interface NavigationRule {
	String fromViewId();

	Class<?> fromController();

	public NavigationCase[] value();
}

package org.springframework.springfaces.mvc.navigation;

public @interface NavigationRule {
	String fromViewId();

	Class<?> fromController();

	public NavigationCase[] value();
}

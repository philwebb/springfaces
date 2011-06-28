package org.springframework.springfaces.mvc.navigation.annotation;

import org.springframework.springfaces.mvc.navigation.NavigationContext;

public interface NavigationFilter {

	// FIXME DC

	public static final NavigationFilter NONE = new NavigationFilter() {
		public boolean matches(NavigationContext context) {
			return true;
		}
	};

	boolean matches(NavigationContext context);
}

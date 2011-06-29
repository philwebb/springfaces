package org.springframework.springfaces.mvc.navigation.annotation;

import org.springframework.springfaces.mvc.navigation.NavigationContext;

/**
 * Filter that can be used to restrict JSF navigation mappings.
 * 
 * @author Phillip Webb
 */
public interface NavigationMappingFilter {

	/**
	 * {@link NavigationMappingFilter} that always returns <tt>true</tt> and hence does not filter any result.
	 */
	public static final NavigationMappingFilter NONE = new NavigationMappingFilter() {
		public boolean matches(NavigationContext context) {
			return true;
		}
	};

	/**
	 * Determine whether the given navigation context matches.
	 * @param context the context to check
	 * @return <tt>true</tt> if the context matches
	 */
	boolean matches(NavigationContext context);
}

package org.springframework.springfaces.mvc.navigation.annotation;

import org.springframework.springfaces.mvc.navigation.NavigationContext;

/**
 * Filter that can be used to restrict JSF navigation mappings.
 * 
 * @author Phillip Webb
 */
public interface NavigationMappingFilter {

	/**
	 * Determine whether the given navigation context matches.
	 * @param context the context to check
	 * @return <tt>true</tt> if the context matches
	 */
	boolean matches(NavigationContext context);
}

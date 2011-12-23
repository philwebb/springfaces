package org.springframework.springfaces.mvc.navigation.annotation;

import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.util.Assert;

/**
 * A composite {@link NavigationMappingFilter} that matches when all the specified filters match.
 * 
 * @author Phillip Webb
 */
public class CompositeNavigationMappingFilter implements NavigationMappingFilter {

	private NavigationMappingFilter[] filters;

	/**
	 * Create a new {@link CompositeNavigationMappingFilter}.
	 * @param filters the composite filters
	 */
	public CompositeNavigationMappingFilter(NavigationMappingFilter... filters) {
		Assert.notNull(filters, "Filters must not be null");
		Assert.noNullElements(filters, "Filters must not contain null elements");
		this.filters = filters;
	}

	public boolean matches(NavigationContext context) {
		for (NavigationMappingFilter filter : this.filters) {
			if (!filter.matches(context)) {
				return false;
			}
		}
		return true;
	}
}

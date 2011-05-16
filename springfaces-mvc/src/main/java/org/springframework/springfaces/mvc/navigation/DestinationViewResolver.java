package org.springframework.springfaces.mvc.navigation;

import java.util.Locale;

import org.springframework.web.servlet.View;

/**
 * Interface to be implemented by objects that can resolve navigation destinations into views.
 * 
 * @see NavigationOutcome
 * @see NavigationOutcomeResolver
 * 
 * @author Phillip Webb
 */
public interface DestinationViewResolver {

	/**
	 * Resolve the given destination to a view.
	 * <p>
	 * Note: To allow for DestinationViewResolver chaining, a DestinationViewResolver should return <code>null</code> if
	 * the destination cannot be resolved .
	 * @param destination the view destination (as obtained from a {@link NavigationOutcomeResolver})
	 * @param locale the locale in which to resolve the view. ViewResolvers that support internationalization should
	 * respect this
	 * @return the {@link View} object, or <code>null</code> if not found (optional, to allow for ViewResolver chaining)
	 * @throws Exception if the view cannot be resolved (typically in case of problems creating an actual View object)
	 */
	View resolveDestination(Object destination, Locale locale) throws Exception;

}

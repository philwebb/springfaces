package org.springframework.springfaces.mvc.navigation;

import java.util.Locale;

import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.web.servlet.ModelAndView;

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
	 * Resolve the given destination to a view. The returned {@link ModelAndView} must contain a non-null
	 * {@link ModelAndView#getView() view} and can optionally contain a {@link ModelAndView#getModel() model}.
	 * <p>
	 * Note: To allow for DestinationViewResolver chaining, a DestinationViewResolver should return <code>null</code> if
	 * the destination cannot be resolved .
	 * @param destination the view destination (as obtained from a {@link NavigationOutcomeResolver})
	 * @param locale the locale in which to resolve the view. ViewResolvers that support internationalization should
	 * respect this
	 * @param model the {@link SpringFacesModel} at the time the view was resolved. Certain resolver implementations may
	 * choose to propagate this model in the returned {@link ModelAndView}.
	 * @return the {@link ModelAndView} object, or <code>null</code> if not found (optional, to allow for ViewResolver
	 * chaining)
	 * @throws Exception if the view cannot be resolved (typically in case of problems creating an actual View object)
	 */
	ModelAndView resolveDestination(Object destination, Locale locale, SpringFacesModel model) throws Exception;
}

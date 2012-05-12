/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.mvc.navigation;

import java.util.Locale;

import javax.faces.context.FacesContext;

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
	 * @param context the faces context
	 * @param destination the view destination (as obtained from a {@link NavigationOutcomeResolver})
	 * @param locale the locale in which to resolve the view. ViewResolvers that support internationalization should
	 * respect this
	 * @param model the {@link SpringFacesModel} at the time the view was resolved. Certain resolver implementations may
	 * choose to propagate this model in the returned {@link ModelAndView}.
	 * @return the {@link ModelAndView} object, or <code>null</code> if not found (optional, to allow for ViewResolver
	 * chaining)
	 * @throws Exception if the view cannot be resolved (typically in case of problems creating an actual View object)
	 */
	ModelAndView resolveDestination(FacesContext context, Object destination, Locale locale, SpringFacesModel model)
			throws Exception;
}

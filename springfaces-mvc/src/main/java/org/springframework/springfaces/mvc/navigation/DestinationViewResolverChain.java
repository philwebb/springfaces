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

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * {@link DestinationViewResolver} that allows several resolvers to be chained together. The first resolver that returns
 * a <tt>non-null</tt> {@link View} is used.
 * @author Phillip Webb
 */
public class DestinationViewResolverChain implements DestinationViewResolver {

	private List<DestinationViewResolver> resolvers;

	public ModelAndView resolveDestination(FacesContext context, Object destination, Locale locale,
			SpringFacesModel model) throws Exception {
		if (this.resolvers != null) {
			for (DestinationViewResolver resolver : this.resolvers) {
				ModelAndView view = resolver.resolveDestination(context, destination, locale, model);
				if (view != null) {
					return view;
				}
			}
		}
		return null;
	}

	/**
	 * Set the list of resolvers that will be used when resolving a destination.
	 * @param resolvers the list of resolvers
	 */
	public void setResolvers(List<DestinationViewResolver> resolvers) {
		this.resolvers = resolvers;
	}
}

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

import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

/**
 * {@link NavigationOutcomeResolver} that allows several resolvers to be chained together.
 * @author Phillip Webb
 */
public class NavigationOutcomeResolverChain implements NavigationOutcomeResolver {

	private List<NavigationOutcomeResolver> resolvers;

	public boolean canResolve(FacesContext facesContext, NavigationContext navigationContext) {
		return findResolver(facesContext, navigationContext) != null;
	}

	public NavigationOutcome resolve(FacesContext facesContext, NavigationContext navigationContext) throws Exception {
		NavigationOutcomeResolver resolver = findResolver(facesContext, navigationContext);
		Assert.state(resolver != null,
				"Unable to find resolver for navigation outcome '" + navigationContext.getOutcome() + "'");
		return resolver.resolve(facesContext, navigationContext);
	}

	private NavigationOutcomeResolver findResolver(FacesContext facesContext, NavigationContext navigationContext) {
		NavigationOutcomeResolver found = null;
		if (this.resolvers != null) {
			for (NavigationOutcomeResolver resolver : this.resolvers) {
				if (resolver.canResolve(facesContext, navigationContext)) {
					Assert.state(found == null, "Duplicate resolvers found for navigation outcome '"
							+ navigationContext.getOutcome() + "'");
					found = resolver;
				}
			}
		}
		return found;
	}

	/**
	 * Set the list of resolvers that will be used when resolving a destination.
	 * @param resolvers the list of resolvers
	 */
	public void setResolvers(List<NavigationOutcomeResolver> resolvers) {
		this.resolvers = resolvers;
	}

	/**
	 * Returns the list of resolvers that will be used when resolving a destination.
	 * @return the resolvers the list of resolvers
	 */
	public List<NavigationOutcomeResolver> getResolvers() {
		return this.resolvers;
	}
}

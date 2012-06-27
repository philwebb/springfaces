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

import javax.faces.context.FacesContext;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A {@link NavigationOutcomeResolver} to handle implicit MVC navigation outcomes. Implicit outcomes can be embedded
 * within a JSF page (if JSF implicit navigation is enabled) or used within standard JSF navigation rules. Outcomes are
 * prefixed in order to distinguish them from regular JSF outcomes. The default prefix is <tt>spring:</tt>, however,
 * this can be {@link #setPrefix changed} if necessary. This resolver will ultimately return a String
 * {@link NavigationOutcome#getDestination() destination}, for example the implicit view "<tt>spring:redirect:/home</tt>
 * " will return an outcome containing the destination "<tt>redirect:/home</tt>".
 * @author Phillip Webb
 */
public class ImplicitNavigationOutcomeResolver implements NavigationOutcomeResolver {

	private String prefix = "spring:";

	public boolean canResolve(FacesContext facesContext, NavigationContext navigationContext) {
		return canResolve(navigationContext.getDefaultDestinationViewId())
				|| canResolve(navigationContext.getOutcome());
	}

	private boolean canResolve(String value) {
		return (StringUtils.hasLength(value) && value.startsWith(this.prefix));
	}

	public NavigationOutcome resolve(FacesContext facesContext, NavigationContext navigationContext) throws Exception {
		String destination = null;
		if (canResolve(navigationContext.getDefaultDestinationViewId())) {
			destination = resolve(navigationContext.getDefaultDestinationViewId());
		} else if (canResolve(navigationContext.getOutcome())) {
			destination = resolve(navigationContext.getOutcome());
		}
		Assert.state(destination != null);
		return new NavigationOutcome(destination, null);
	}

	private String resolve(String value) {
		String destination = value.substring(this.prefix.length());
		Assert.state(StringUtils.hasLength(destination),
				"The destination must be specified for an implicit MVC navigation prefixed '" + this.prefix + "'");
		return destination;
	}

	/**
	 * Sets the prefix that will be used to distinguish a MVC implicit outcome from a regular JSF outcome.
	 * @param prefix the prefix for implicit MVC navigation
	 */
	public void setPrefix(String prefix) {
		Assert.hasLength(prefix, "Prefix must contain at least character");
		this.prefix = prefix;
	}
}

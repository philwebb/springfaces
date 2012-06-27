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
package org.springframework.springfaces.mvc.navigation.annotation;

import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.util.Assert;

/**
 * A composite {@link NavigationMappingFilter} that matches when all the specified filters match.
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

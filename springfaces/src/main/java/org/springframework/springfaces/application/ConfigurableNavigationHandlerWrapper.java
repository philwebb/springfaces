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
package org.springframework.springfaces.application;

import java.util.Map;
import java.util.Set;

import javax.faces.FacesWrapper;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;

/**
 * Provides a simple implementation of {@link ConfigurableNavigationHandler} that can be subclassed by developers
 * wishing to provide specialized behavior to an existing {@link ConfigurableNavigationHandler instance}. The default
 * implementation of all methods is to call through to the wrapped {@link ConfigurableNavigationHandler}.
 * @author Phillip Webb
 */
public abstract class ConfigurableNavigationHandlerWrapper extends ConfigurableNavigationHandler implements
		FacesWrapper<ConfigurableNavigationHandler> {

	public abstract ConfigurableNavigationHandler getWrapped();

	@Override
	public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
		return getWrapped().getNavigationCase(context, fromAction, outcome);
	}

	@Override
	public Map<String, Set<NavigationCase>> getNavigationCases() {
		return getWrapped().getNavigationCases();
	}

	@Override
	public void performNavigation(String outcome) {
		getWrapped().performNavigation(outcome);
	}

	@Override
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {
		getWrapped().handleNavigation(context, fromAction, outcome);
	}
}

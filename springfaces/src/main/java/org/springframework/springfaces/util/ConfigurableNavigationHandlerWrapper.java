package org.springframework.springfaces.util;

import java.util.Map;
import java.util.Set;

import javax.faces.FacesWrapper;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;

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
	public void handleNavigation(FacesContext context, String fromAction, String outcome) {
		getWrapped().handleNavigation(context, fromAction, outcome);
	}
}

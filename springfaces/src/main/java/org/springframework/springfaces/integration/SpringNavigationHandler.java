package org.springframework.springfaces.integration;

import javax.faces.application.ConfigurableNavigationHandler;

import org.springframework.springfaces.application.NavigationHandlerFactory;
import org.springframework.springfaces.dunno.SpringFacesUtils;
import org.springframework.springfaces.util.ConfigurableNavigationHandlerWrapper;

public class SpringNavigationHandler extends ConfigurableNavigationHandlerWrapper {

	private ConfigurableNavigationHandler rootDelegate;
	private ConfigurableNavigationHandler delegate;

	public SpringNavigationHandler(ConfigurableNavigationHandler delegate) {
		this.rootDelegate = delegate;
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		if (delegate == null) {
			setupDelegate();
		}
		return delegate;
	}

	private void setupDelegate() {
		delegate = rootDelegate;
		for (NavigationHandlerFactory factory : SpringFacesUtils.getBeans(NavigationHandlerFactory.class)) {
			//FIXME log detail
			delegate = factory.newNavigationHandler(delegate);
		}
	}

	//FIXME all our integration points are looking very similar, can we make a utility

}

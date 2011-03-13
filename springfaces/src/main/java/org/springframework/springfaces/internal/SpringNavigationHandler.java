package org.springframework.springfaces.internal;

import javax.faces.application.ConfigurableNavigationHandler;

import org.springframework.springfaces.util.ConfigurableNavigationHandlerWrapper;

public class SpringNavigationHandler extends ConfigurableNavigationHandlerWrapper {

	private WrapperHandler<ConfigurableNavigationHandler> wrapperHandler;

	public SpringNavigationHandler(ConfigurableNavigationHandler delegate) {
		this.wrapperHandler = WrapperHandler.get(ConfigurableNavigationHandler.class, delegate);
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return wrapperHandler.getWrapped();
	}

}

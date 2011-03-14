package org.springframework.springfaces.internal;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationHandler;

import org.springframework.springfaces.util.ConfigurableNavigationHandlerWrapper;

/**
 * A JSF {@link NavigationHandler} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
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

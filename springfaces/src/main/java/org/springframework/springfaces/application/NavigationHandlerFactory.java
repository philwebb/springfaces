package org.springframework.springfaces.application;

import javax.faces.application.ConfigurableNavigationHandler;

public interface NavigationHandlerFactory {

	public ConfigurableNavigationHandler newNavigationHandler(ConfigurableNavigationHandler delegate);

}

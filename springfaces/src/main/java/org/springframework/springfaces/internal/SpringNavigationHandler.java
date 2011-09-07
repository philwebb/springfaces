package org.springframework.springfaces.internal;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.springfaces.application.ConfigurableNavigationHandlerWrapper;

/**
 * A JSF {@link NavigationHandler} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringNavigationHandler extends ConfigurableNavigationHandlerWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private WrapperHandler<ConfigurableNavigationHandler> wrapperHandler;

	public SpringNavigationHandler(ConfigurableNavigationHandler delegate) {
		if (logger.isDebugEnabled()) {
			logger.debug("Wrapping NavigationHandler " + delegate.getClass() + " to provide integration with Spring");
		}
		this.wrapperHandler = WrapperHandler.get(ConfigurableNavigationHandler.class, delegate);
	}

	@Override
	public ConfigurableNavigationHandler getWrapped() {
		return wrapperHandler.getWrapped();
	}

}

package org.springframework.springfaces.internal;

import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JSF {@link Application} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringApplication extends ApplicationWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private WrapperHandler<Application> wrapperHandler;

	public SpringApplication(Application delegate) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Wrapping Application " + delegate.getClass() + " to provide integration with Spring");
		}
		this.wrapperHandler = WrapperHandler.get(Application.class, delegate);
	}

	@Override
	public Application getWrapped() {
		return this.wrapperHandler.getWrapped();
	}
}
package org.springframework.springfaces.internal;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A JSF {@link ViewHandler} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringViewHandler extends ViewHandlerWrapper {

	private final Log logger = LogFactory.getLog(getClass());

	private WrapperHandler<ViewHandler> wrapperHandler;

	public SpringViewHandler(ViewHandler delegate) {
		if (logger.isDebugEnabled()) {
			logger.debug("Wrapping ViewHandler " + delegate.getClass() + " to provide integration with Spring");
		}
		this.wrapperHandler = WrapperHandler.get(ViewHandler.class, delegate);
	}

	@Override
	public ViewHandler getWrapped() {
		return wrapperHandler.getWrapped();
	}
}

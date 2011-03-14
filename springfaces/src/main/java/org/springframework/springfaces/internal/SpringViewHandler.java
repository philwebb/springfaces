package org.springframework.springfaces.internal;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;

/**
 * A JSF {@link ViewHandler} that provides integration with Spring.
 * 
 * @author Phillip Webb
 */
public class SpringViewHandler extends ViewHandlerWrapper {

	private WrapperHandler<ViewHandler> wrapperHandler;

	public SpringViewHandler(ViewHandler delegate) {
		this.wrapperHandler = WrapperHandler.get(ViewHandler.class, delegate);
	}

	@Override
	public ViewHandler getWrapped() {
		return wrapperHandler.getWrapped();
	}
}

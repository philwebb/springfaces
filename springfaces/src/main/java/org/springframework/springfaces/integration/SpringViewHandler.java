package org.springframework.springfaces.integration;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;

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

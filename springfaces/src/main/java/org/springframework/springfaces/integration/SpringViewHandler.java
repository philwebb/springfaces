package org.springframework.springfaces.integration;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;

import org.springframework.springfaces.application.ViewHandlerFactory;
import org.springframework.springfaces.dunno.SpringFacesUtils;


public class SpringViewHandler extends ViewHandlerWrapper {

	private ViewHandler rootDelegate;
	private ViewHandler delegate;

	public SpringViewHandler(ViewHandler delegate) {
		this.rootDelegate = delegate;
	}

	@Override
	public ViewHandler getWrapped() {
		if (delegate == null) {
			setupDelegate();
		}
		return delegate;
	}

	private void setupDelegate() {
		delegate = rootDelegate;
		for (ViewHandlerFactory factory : SpringFacesUtils.getBeans(ViewHandlerFactory.class)) {
			//FIXME log detail
			delegate = factory.newViewHandler(delegate);
		}
	}
}

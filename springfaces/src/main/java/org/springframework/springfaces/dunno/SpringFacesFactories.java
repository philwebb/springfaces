package org.springframework.springfaces.dunno;

import javax.faces.application.ViewHandler;
import javax.faces.render.ResponseStateManager;

public class SpringFacesFactories implements ResponseStateManagerFactory, ViewHandlerFactory {

	public SpringFacesFactories() {
		System.out.println("adding faces factory");
	}

	public ViewHandler newViewHandler(ViewHandler delegate) {
		return new SpringFacesViewHandler(delegate);
	}

	public ResponseStateManager newResponseStateManager(ResponseStateManager delegate, String renderKitId) {
		return delegate;
	}
}

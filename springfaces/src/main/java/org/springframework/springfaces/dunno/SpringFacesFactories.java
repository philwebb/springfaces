package org.springframework.springfaces.dunno;

import javax.faces.application.ViewHandler;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.application.SpringFacesViewHandler;
import org.springframework.springfaces.application.ViewHandlerFactory;
import org.springframework.springfaces.render.ResponseStateManagerFactory;
import org.springframework.springfaces.render.SpringFacesResponseStateManager;

public class SpringFacesFactories implements ResponseStateManagerFactory, ViewHandlerFactory {

	public SpringFacesFactories() {
		System.out.println("adding faces factory");
	}

	public ViewHandler newViewHandler(ViewHandler delegate) {
		return new SpringFacesViewHandler(delegate);
	}

	public ResponseStateManager newResponseStateManager(ResponseStateManager delegate) {
		return new SpringFacesResponseStateManager(delegate);
	}
}

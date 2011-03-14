package org.springframework.springfaces.mvc;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.internal.SpringApplicationFactory;

public class SpringFacesFactories implements FacesWrapperFactory<Object> {

	public Object newWrapper(Class<?> typeClass, Object delegate) {
		if (delegate instanceof ResponseStateManager) {
			return new SpringFacesResponseStateManager((ResponseStateManager) delegate);
		}
		if (delegate instanceof ViewHandler) {
			return new SpringFacesViewHandler((ViewHandler) delegate);
		}
		if (delegate instanceof Application) {
			return new SpringApplicationFactory((ApplicationFactory) delegate);
		}
		return null;
	}
}

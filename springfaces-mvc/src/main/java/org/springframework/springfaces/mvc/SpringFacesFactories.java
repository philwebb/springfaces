package org.springframework.springfaces.mvc;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.mvc.internal.SpringFacesMvcNavigationHandler;
import org.springframework.springfaces.mvc.internal.SpringFacesResponseStateManager;
import org.springframework.springfaces.mvc.internal.SpringFacesViewHandler;

public class SpringFacesFactories implements FacesWrapperFactory<Object> {

	public Object newWrapper(Class<?> typeClass, Object delegate) {
		if (delegate instanceof ResponseStateManager) {
			return new SpringFacesResponseStateManager((ResponseStateManager) delegate);
		}
		if (delegate instanceof ViewHandler) {
			return new SpringFacesViewHandler((ViewHandler) delegate);
		}
		if (ConfigurableNavigationHandler.class.equals(typeClass)) {
			return new SpringFacesMvcNavigationHandler((ConfigurableNavigationHandler) delegate);
		}
		return null;
	}
}

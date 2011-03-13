package org.springframework.springfaces.mvc;

import javax.faces.application.ViewHandler;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesWrapperFactoryBean;

public class SpringFacesFactories implements FacesWrapperFactoryBean<Object> {

	public Object newWrapper(Object delegate) {
		if (delegate instanceof ResponseStateManager) {
			return new SpringFacesResponseStateManager((ResponseStateManager) delegate);
		}
		if (delegate instanceof ViewHandler) {
			return new SpringFacesViewHandler((ViewHandler) delegate);
		}
		return null;
	}
}
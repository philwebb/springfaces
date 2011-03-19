package org.springframework.springfaces.mvc.servlet;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.mvc.internal.MvcNavigationHandler;
import org.springframework.springfaces.mvc.internal.MvcResponseStateManager;
import org.springframework.springfaces.mvc.internal.MvcViewHandler;
import org.springframework.springfaces.render.FacesViewStateHandler;
import org.springframework.util.Assert;

public class SpringFacesFactories implements FacesWrapperFactory<Object> {

	private FacesViewStateHandler facesViewStateHandler;
	private ViewIdResolver viewIdResolver;

	public SpringFacesFactories(FacesViewStateHandler facesViewStateHandler, ViewIdResolver viewIdResolver) {
		Assert.notNull(facesViewStateHandler, "FacesViewStateHandler must not be null");
		Assert.notNull(viewIdResolver, "ViewIDResolver must not be null");
		this.facesViewStateHandler = facesViewStateHandler;
		this.viewIdResolver = viewIdResolver;
	}

	public Object newWrapper(Class<?> typeClass, Object delegate) {
		if (delegate instanceof ResponseStateManager) {
			return new MvcResponseStateManager((ResponseStateManager) delegate, facesViewStateHandler);
		}
		if (delegate instanceof ViewHandler) {
			return new MvcViewHandler((ViewHandler) delegate, viewIdResolver);
		}
		if (ConfigurableNavigationHandler.class.equals(typeClass)) {
			return new MvcNavigationHandler((ConfigurableNavigationHandler) delegate);
		}
		return null;
	}
}

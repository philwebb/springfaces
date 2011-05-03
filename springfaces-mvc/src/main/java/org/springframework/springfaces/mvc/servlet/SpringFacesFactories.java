package org.springframework.springfaces.mvc.servlet;

import javax.el.CompositeELResolver;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.mvc.expression.el.SpringBeanMvcELResolver;
import org.springframework.springfaces.mvc.internal.DefaultDestinationRegistry;
import org.springframework.springfaces.mvc.internal.MvcNavigationHandler;
import org.springframework.springfaces.mvc.internal.MvcResponseStateManager;
import org.springframework.springfaces.mvc.internal.MvcViewHandler;
import org.springframework.springfaces.mvc.navigation.ImplicitNavigationOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.springfaces.render.FacesViewStateHandler;
import org.springframework.util.Assert;

public class SpringFacesFactories implements FacesWrapperFactory<Object> {

	private FacesViewStateHandler facesViewStateHandler;
	private ViewIdResolver viewIdResolver;
	private NavigationOutcomeResolver navigationOutcomeResolver;
	private DefaultDestinationRegistry destinationRegistry;

	public SpringFacesFactories(FacesViewStateHandler facesViewStateHandler, ViewIdResolver viewIdResolver) {
		Assert.notNull(facesViewStateHandler, "FacesViewStateHandler must not be null");
		Assert.notNull(viewIdResolver, "ViewIDResolver must not be null");
		this.facesViewStateHandler = facesViewStateHandler;
		this.viewIdResolver = viewIdResolver;
		// FIXME
		this.navigationOutcomeResolver = new ImplicitNavigationOutcomeResolver();
		this.destinationRegistry = new DefaultDestinationRegistry();
	}

	public Object newWrapper(Class<?> typeClass, Object delegate) {
		if (delegate instanceof ResponseStateManager) {
			return new MvcResponseStateManager((ResponseStateManager) delegate, facesViewStateHandler);
		}
		if (delegate instanceof ViewHandler) {
			return new MvcViewHandler((ViewHandler) delegate, viewIdResolver, destinationRegistry);
		}
		if (ConfigurableNavigationHandler.class.equals(typeClass)) {
			return new MvcNavigationHandler((ConfigurableNavigationHandler) delegate, navigationOutcomeResolver,
					destinationRegistry);
		}
		if (CompositeELResolver.class.equals(typeClass)) {
			((CompositeELResolver) delegate).add(new SpringBeanMvcELResolver());
		}
		return null;
	}
}

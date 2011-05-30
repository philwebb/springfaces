package org.springframework.springfaces.mvc.servlet;

import javax.el.CompositeELResolver;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.event.ActionListener;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.render.ResponseStateManager;

import org.springframework.context.ApplicationListener;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.event.PostConstructApplicationSpringFacesEvent;
import org.springframework.springfaces.mvc.expression.el.ImplicitMvcFacesELResolver;
import org.springframework.springfaces.mvc.expression.el.SpringBeanMvcELResolver;
import org.springframework.springfaces.mvc.expression.el.SpringFacesMvcModelELResolver;
import org.springframework.springfaces.mvc.internal.MvcNavigationActionListener;
import org.springframework.springfaces.mvc.internal.MvcNavigationHandler;
import org.springframework.springfaces.mvc.internal.MvcNavigationSystemEventListener;
import org.springframework.springfaces.mvc.internal.MvcResponseStateManager;
import org.springframework.springfaces.mvc.internal.MvcViewHandler;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.navigation.ImplicitNavigationOutcomeResolver;
import org.springframework.springfaces.render.FacesViewStateHandler;
import org.springframework.util.Assert;

public class SpringFacesFactories implements FacesWrapperFactory<Object>,
		ApplicationListener<PostConstructApplicationSpringFacesEvent> {

	private FacesViewStateHandler facesViewStateHandler;
	private DestinationViewResolver destinationViewResolver;
	private ImplicitNavigationOutcomeResolver navigationOutcomeResolver;
	private MvcNavigationSystemEventListener navigationSystemEventListener = new MvcNavigationSystemEventListener();

	public SpringFacesFactories(FacesViewStateHandler facesViewStateHandler,
			DestinationViewResolver destinationViewResolver) {
		Assert.notNull(facesViewStateHandler, "FacesViewStateHandler must not be null");
		Assert.notNull(destinationViewResolver, "DestinationViewResolver must not be null");
		this.facesViewStateHandler = facesViewStateHandler;
		// FIXME
		this.navigationOutcomeResolver = new ImplicitNavigationOutcomeResolver();
		this.destinationViewResolver = destinationViewResolver;
	}

	// FIXME consider rename of internal package

	public Object newWrapper(Class<?> typeClass, Object delegate) {
		if (delegate instanceof ResponseStateManager) {
			return new MvcResponseStateManager((ResponseStateManager) delegate, facesViewStateHandler);
		}
		if (delegate instanceof ViewHandler) {
			return new MvcViewHandler((ViewHandler) delegate, destinationViewResolver);
		}
		if (ConfigurableNavigationHandler.class.equals(typeClass)) {
			return new MvcNavigationHandler((ConfigurableNavigationHandler) delegate, navigationOutcomeResolver);
		}
		if (ActionListener.class.equals(typeClass)) {
			return new MvcNavigationActionListener((ActionListener) delegate);
		}
		if (CompositeELResolver.class.equals(typeClass)) {
			CompositeELResolver compositeELResolver = (CompositeELResolver) delegate;
			compositeELResolver.add(new SpringBeanMvcELResolver());
			compositeELResolver.add(new SpringFacesMvcModelELResolver());
			compositeELResolver.add(new ImplicitMvcFacesELResolver());
		}
		return null;
	}

	public void onApplicationEvent(PostConstructApplicationSpringFacesEvent event) {
		event.getSource().subscribeToEvent(PreRenderComponentEvent.class, navigationSystemEventListener);
	}
}

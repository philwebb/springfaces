/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.springframework.springfaces.mvc.expression.el.ImplicitSpringFacesELResolver;
import org.springframework.springfaces.mvc.expression.el.SpringFacesBeanELResolver;
import org.springframework.springfaces.mvc.expression.el.SpringFacesModelELResolver;
import org.springframework.springfaces.mvc.internal.MvcNavigationActionListener;
import org.springframework.springfaces.mvc.internal.MvcNavigationHandler;
import org.springframework.springfaces.mvc.internal.MvcNavigationSystemEventListener;
import org.springframework.springfaces.mvc.internal.MvcResponseStateManager;
import org.springframework.springfaces.mvc.internal.MvcViewHandler;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.springfaces.mvc.navigation.ImplicitNavigationOutcomeResolver;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;
import org.springframework.springfaces.mvc.render.FacesViewStateHandler;
import org.springframework.util.Assert;

public class SpringFacesFactories implements FacesWrapperFactory<Object>,
		ApplicationListener<PostConstructApplicationSpringFacesEvent> {

	// FIXME rename this to MVC?

	private FacesViewStateHandler facesViewStateHandler;
	private DestinationViewResolver destinationViewResolver;
	private NavigationOutcomeResolver navigationOutcomeResolver;
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
			return new MvcResponseStateManager((ResponseStateManager) delegate, this.facesViewStateHandler);
		}
		if (delegate instanceof ViewHandler) {
			return new MvcViewHandler((ViewHandler) delegate, this.destinationViewResolver);
		}
		if (ConfigurableNavigationHandler.class.equals(typeClass)) {
			return new MvcNavigationHandler((ConfigurableNavigationHandler) delegate, this.navigationOutcomeResolver);
		}
		if (ActionListener.class.equals(typeClass)) {
			return new MvcNavigationActionListener((ActionListener) delegate);
		}
		if (CompositeELResolver.class.equals(typeClass)) {
			CompositeELResolver compositeELResolver = (CompositeELResolver) delegate;
			compositeELResolver.add(new SpringFacesBeanELResolver());
			compositeELResolver.add(new SpringFacesModelELResolver());
			compositeELResolver.add(new ImplicitSpringFacesELResolver());
		}
		return null;
	}

	public void onApplicationEvent(PostConstructApplicationSpringFacesEvent event) {
		event.getSource().subscribeToEvent(PreRenderComponentEvent.class, this.navigationSystemEventListener);
	}

	public void setNavigationOutcomeResolver(NavigationOutcomeResolver navigationOutcomeResolver) {
		this.navigationOutcomeResolver = navigationOutcomeResolver;
	}
}

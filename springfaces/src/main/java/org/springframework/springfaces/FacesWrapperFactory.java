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
package org.springframework.springfaces;

import javax.el.CompositeELResolver;
import javax.faces.FacesWrapper;
import javax.faces.application.Application;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.event.ActionListener;
import javax.faces.render.RenderKit;
import javax.faces.render.ResponseStateManager;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.springfaces.render.RenderKitIdAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * A factory that to create wrappers around various JSF objects. All factories from the Spring
 * {@link WebApplicationContext context} containing the {@link SpringFacesIntegration} bean will be consulted when any
 * of the following JSF objects are created:
 * <ul>
 * <li>{@link ActionListener}</li>
 * <li>{@link Application}</li>
 * <li>{@link ConfigurableNavigationHandler}</li>
 * <li>{@link RenderKit}</li>
 * <li>{@link ResponseStateManager}</li>
 * <li>{@link ViewHandler}</li>
 * <li>{@link ExceptionHandler}</li>
 * <li>{@link CompositeELResolver}</li>
 * </ul>
 * A <tt>FacesWrapperFactory</tt> can generically declare the JSF object type that it wraps. Factories will be filtered
 * accordingly, with {@link #newWrapper} only being invoked for matching JSF objects.
 * <p>
 * Factories can implement the {@link Ordered} interface or use the {@link Order} annotation if a specific invocation
 * order is required.
 * <p>
 * When working with {@link CompositeELResolver}s the {@link CompositeELResolver#add add} method of the delegate should
 * be used and the original delegate returned.
 * 
 * @author Phillip Webb
 * @param <T> the type of class to be wrapped.
 * @see FacesWrapperFactory
 * @see RenderKitIdAware
 */
public interface FacesWrapperFactory<T> {

	/**
	 * Factory method that can be used to wrap the specified JSF object.
	 * @param typeClass the JSF Object type being wrapped
	 * @param wrapped the existing JSF object that should be wrapped
	 * @return a {@link FacesWrapper wrapped} object that it is itself an implementation of <tt>typeClass</tt> or
	 * <tt>null</tt> if no wrapping is required
	 */
	T newWrapper(Class<?> typeClass, T wrapped);
}

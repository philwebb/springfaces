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
package org.springframework.springfaces.mvc.context;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.webapp.FacesServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.NamedThreadLocal;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.servlet.FacesHandlerInterceptor;
import org.springframework.springfaces.mvc.servlet.view.FacesRenderedView;
import org.springframework.springfaces.mvc.servlet.view.FacesView;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.View;

/**
 * Context for a single MVC request that may result in a JSF response. A {@link SpringFacesContext} instance is
 * associated with a Spring MVC request from the point that the {@link HandlerMapping#getHandler handler} is obtained.
 * The instance remains active and bound to the current thread until the MVC request has completed.
 * <p>
 * The {@link SpringFacesContext} can be used to access the JSF {@link #getFacesContext facesContext} regardless of the
 * fact that the request is not being processed by the {@link FacesServlet}.
 * 
 * @author Phillip Webb
 * @see #getCurrentInstance()
 * @see FacesHandlerInterceptor
 */
public abstract class SpringFacesContext {

	private static ThreadLocal<SpringFacesContext> instance = new NamedThreadLocal<SpringFacesContext>(
			"Spring Faces Context");

	/**
	 * Returns a JSF {@link FacesContext} for the current request. <b>NOTE:</b> Any {@link FacesContext} successfully
	 * obtained using this method should be {@link FacesContext#release() released} after use. <code>
	 * FacesContext context = springFacesContext.getFacesContext(true);
	 * try {
	 *   // work with context
	 * } finally {
	 *   context.release();
	 * }
	 * </code>
	 * @return a {@link FacesContext} instance
	 */
	public abstract FacesContext getFacesContext();

	/**
	 * Returns the {@link WebApplicationContext} associated with this context.
	 * @return a {@link WebApplicationContext} instance
	 */
	public abstract WebApplicationContext getWebApplicationContext();

	/**
	 * Returns the MVC Handler associated with this context.
	 * @return the MVC handler
	 */
	public abstract Object getHandler();

	/**
	 * Returns the MVC controller associated with this context. If the {@link #getHandler() handler} is a Spring 3.1
	 * {@link HandlerMethod} this method will return the underling {@link HandlerMethod#getBean()}, in all other cases
	 * this method is identical to {@link #getHandler()}.
	 * @return the MVC controller
	 */
	public abstract Object getController();

	/**
	 * Render the specified view and model.
	 * <p>
	 * If the specified view is <b>not</b> a {@link FacesView} it should be rendered directly to the current response
	 * (either via {@link View#render(Map, HttpServletRequest, HttpServletResponse)} or when possible
	 * {@link FacesRenderedView#render(Map, FacesContext)}.
	 * <p>
	 * If the view is a {@link FacesView} the implementation must make the appropriate JSF calls to construct and render
	 * the view from the {@link FacesView#getViewArtifact() view artifact}. The {@link #getRendering()} method must
	 * return a non-null value during the rendering of {@link FacesView}s. This method can be called to both initiate
	 * rendering or replace the view that is currently being rendered. When a replacing an existing view implementations
	 * should set the {@link FacesContext#setViewRoot(javax.faces.component.UIViewRoot) FacesContext.viewRoot} and allow
	 * the active lifecyle to render the response.
	 * 
	 * @param view the view to render
	 * @param model the model or <tt>null</tt>
	 */
	public abstract void render(View view, Map<String, Object> model);

	/**
	 * Return the {@link ModelAndViewArtifact} that is currently being {@link #render rendered} or <tt>null</tt> if no
	 * Spring Faces MVC request is being rendered.
	 * @return the {@link ModelAndViewArtifact} being rendered or <tt>null</tt>
	 */
	public abstract ModelAndViewArtifact getRendering();

	/**
	 * Returns the current {@link SpringFacesContext} instance or <tt>null</tt> the context is unavailable.
	 * @return the {@link SpringFacesContext} or <tt>null</tt>
	 */
	public static SpringFacesContext getCurrentInstance() {
		return getCurrentInstance(false);
	}

	/**
	 * Returns the current {@link SpringFacesContext}.
	 * @param required <tt>true</tt> if an {@link IllegalStateException} should be thrown when the context cannot be
	 * obtained. <tt>false</tt> if <tt>null</tt> should be returned when the context cannot be obtained
	 * @return the {@link SpringFacesContext} (or <tt>null</tt> if <tt>required</tt> is <tt>false</tt>)
	 */
	public static SpringFacesContext getCurrentInstance(boolean required) {
		SpringFacesContext context = instance.get();
		if (context == null && required) {
			throw new IllegalStateException("Unable to obtain the SpringFacesContext, perhaps "
					+ FacesHandlerInterceptor.class.getSimpleName() + " has not been registered with Spring");
		}
		return context;
	}

	/**
	 * Protected method that should be used by subclasses to set the current context instance.
	 * @param context the context instance to set or <tt>null</tt> to remove the current instance
	 */
	protected static void setCurrentInstance(SpringFacesContext context) {
		if (context == null) {
			instance.remove();
		} else {
			instance.set(context);
		}
	}

}

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

import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.PartialViewContext;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.springfaces.mvc.servlet.view.FacesRenderedView;
import org.springframework.springfaces.mvc.servlet.view.FacesView;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.View;

/**
 * Default implementation of {@link SpringFacesContext}. This is an internal class that is usually managed via the
 * {@link FacesHandlerInterceptor} and is not expected to be used directly.
 * @see FacesHandlerInterceptor
 * @see SpringFacesContext#getCurrentInstance()
 * @author Phillip Webb
 */
public class DefaultSpringFacesContext extends SpringFacesContext {

	private LifecycleAccessor lifecycleAccessor;
	private WebApplicationContext webApplicationContext;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Object handler;
	private ReferenceCountedFacesContext facesContext = new ReferenceCountedFacesContext();
	private boolean released;
	private ModelAndViewArtifact rendering;

	public DefaultSpringFacesContext(LifecycleAccessor lifecycleAccessor, WebApplicationContext webApplicationContext,
			HttpServletRequest request, HttpServletResponse response, Object handler) {
		Assert.notNull(lifecycleAccessor, "LifecycleAccessor must not be null");
		Assert.notNull(webApplicationContext, "WebApplicationContext must not be null");
		Assert.notNull(request, "Request must not be null");
		Assert.notNull(response, "Response must not be null");
		Assert.notNull(handler, "Handler must not be null");
		this.lifecycleAccessor = lifecycleAccessor;
		this.webApplicationContext = webApplicationContext;
		this.request = request;
		this.response = response;
		this.handler = handler;
		setCurrentInstance(this);
	}

	public void release() {
		this.released = true;
		this.facesContext.releaseDelegate();
		setCurrentInstance(null);
	}

	@Override
	public Object getHandler() {
		checkNotRelased();
		return this.handler;
	}

	@Override
	public Object getController() {
		Object controller = getHandler();
		if ((controller != null) && (controller instanceof HandlerMethod)) {
			controller = ((HandlerMethod) controller).createWithResolvedBean().getBean();
		}
		return controller;
	}

	@Override
	public FacesContext getFacesContext() {
		checkNotRelased();
		this.facesContext.addReference();
		return this.facesContext;
	}

	@Override
	public WebApplicationContext getWebApplicationContext() {
		return this.webApplicationContext;
	}

	@Override
	public void render(View view, Map<String, Object> model) {
		FacesContext context = getFacesContext();
		try {
			if (view instanceof FacesView) {
				render(context, ((FacesView) view).getViewArtifact(), model);
			} else {
				try {
					render(context, view, model);
				} catch (Exception e) {
					ReflectionUtils.rethrowRuntimeException(e);
				}
			}
		} finally {
			context.release();
		}
	}

	private void render(FacesContext context, ViewArtifact viewArtifact, Map<String, Object> model) {
		ModelAndViewArtifact modelAndViewArtifact = new ModelAndViewArtifact(viewArtifact, model);
		if (this.rendering != null) {
			this.rendering = modelAndViewArtifact;
			ViewHandler viewHandler = this.facesContext.getApplication().getViewHandler();
			UIViewRoot viewRoot = viewHandler.createView(this.facesContext, viewArtifact.toString());
			this.facesContext.setViewRoot(viewRoot);
		} else {
			this.rendering = modelAndViewArtifact;
			try {
				Lifecycle lifecycle = this.lifecycleAccessor.getLifecycle();
				lifecycle.execute(context);
				lifecycle.render(context);
			} finally {
				this.rendering = null;
			}
		}
	}

	private void render(FacesContext context, View view, Map<String, Object> model) throws Exception {
		if (view instanceof FacesRenderedView) {
			((FacesRenderedView) view).render(model, context);
		} else {
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			PartialViewContext partialViewContext = context.getPartialViewContext();
			if (partialViewContext.isPartialRequest()) {
				Assert.state(!partialViewContext.isAjaxRequest(), "Unable to render MVC response to Faces AJAX request");
			}
			view.render(model, request, response);
		}
	}

	@Override
	public ModelAndViewArtifact getRendering() {
		return this.rendering;
	}

	private void checkNotRelased() {
		Assert.state(!this.released, "The SpringFacesContext has been released");
	}

	/**
	 * A reference counted wrapper for the {@link FacesContext} that will drop the underlying context when all
	 * referenced have been {@link #release() released}.
	 */
	private class ReferenceCountedFacesContext extends FacesContextWrapper {

		private FacesContext delegate;
		int referenceCount;

		public ReferenceCountedFacesContext() {
		}

		@Override
		public FacesContext getWrapped() {
			if (this.delegate == null) {
				FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
						.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
				ServletContext servletContext = DefaultSpringFacesContext.this.webApplicationContext
						.getServletContext();
				Lifecycle lifecycle = DefaultSpringFacesContext.this.lifecycleAccessor.getLifecycle();
				this.delegate = facesContextFactory.getFacesContext(servletContext,
						DefaultSpringFacesContext.this.request, DefaultSpringFacesContext.this.response, lifecycle);
			}
			return this.delegate;
		}

		public void addReference() {
			this.referenceCount++;
		}

		@Override
		public void release() {
			this.referenceCount--;
			if (this.referenceCount == 0) {
				releaseDelegate();
			}
		}

		public void releaseDelegate() {
			if (this.delegate != null) {
				this.delegate.release();
				this.delegate = null;
			}
		}
	}
}
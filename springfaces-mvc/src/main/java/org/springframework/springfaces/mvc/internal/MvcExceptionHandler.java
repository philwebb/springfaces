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
package org.springframework.springfaces.mvc.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.event.ExceptionQueuedEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.message.ObjectMessageSource;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.util.WebUtils;

/**
 * A JSF {@link ExceptionHandler} to integrate with Spring MVC {@link HandlerExceptionResolver}s and also resolve
 * exception messages from an {@link ObjectMessageSource}.
 * 
 * @author Phillip Webb
 */
public class MvcExceptionHandler extends ExceptionHandlerWrapper {

	private static Set<Class<?>> EXCEPTIONS_TO_UNWRAP;
	static {
		HashSet<Class<?>> unwrappedExceptions = new HashSet<Class<?>>();
		unwrappedExceptions.add(FacesException.class);
		unwrappedExceptions.add(ELException.class);
		unwrappedExceptions.add(EvaluationException.class);
		EXCEPTIONS_TO_UNWRAP = Collections.unmodifiableSet(unwrappedExceptions);
	}

	private ExceptionHandler wrapped;

	private boolean detectAllHandlerExceptionResolvers = true;
	private DestinationAndModelRegistry destinationAndModelRegistry = new DestinationAndModelRegistry();

	public MvcExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.ExceptionHandlerWrapper#handle()
	 */
	@Override
	public void handle() throws FacesException {
		if (SpringFacesContext.getCurrentInstance() != null) {
			handle(SpringFacesContext.getCurrentInstance());
		}
		super.handle();
	}

	private void handle(SpringFacesContext springFacesContext) {
		// FIXME should be injected singleton
		DelegateDispatcherServlet delegate = new DelegateDispatcherServlet();
		delegate.onApplicationEvent(new ContextRefreshedEvent(springFacesContext.getWebApplicationContext()));

		FacesContext facesContext = springFacesContext.getFacesContext();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		Object handler = springFacesContext.getHandler();

		Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();
		while (events.hasNext()) {
			ExceptionQueuedEvent event = events.next();
			Throwable cause = getRootCause(event.getContext().getException());
			if (cause instanceof Exception) {
				try {
					ModelAndView modelAndView = delegate.processHandlerException(request, response, handler,
							(Exception) cause);
					if (modelAndView != null) {
						WebUtils.clearErrorRequestAttributes(request);
						if (modelAndView.isReference()) {
							modelAndView.setView(delegate.resolveViewId(modelAndView.getViewName(),
									FacesUtils.getLocale(facesContext)));
						}
						MvcViewHandler.render(facesContext, modelAndView);
						// FIXME we may need to mark as complete
					}
					// if (modelAndView.isReference()) {
					// // FIXME just example
					// DefaultDestinationViewResolver resolver = new DefaultDestinationViewResolver();
					// resolver.onApplicationEvent(new ContextRefreshedEvent(springFacesContext
					// .getWebApplicationContext()));
					// modelAndView = resolver.resolveDestination(modelAndView.getViewName(),
					// FacesUtils.getLocale(facesContext), new SpringFacesModel(modelAndView.getModel()));
					// }
					//
					// PreRenderComponentEvent actionEvent = null;
					// NavigationOutcome navigationOutcome = new NavigationOutcome(modelAndView.getView(),
					// modelAndView.getModel());
					// String viewId = this.destinationAndModelRegistry.put(facesContext, new DestinationAndModel(
					// navigationOutcome, actionEvent));
					// UIViewRoot newRoot = facesContext.getApplication().getViewHandler()
					// .createView(facesContext, viewId);
					// facesContext.setViewRoot(newRoot);
					// events.remove();
					// }
					// facesContext.getExternalContext().redirect("http://www.google.com");
					events.remove();
				} catch (Exception e) {
					ReflectionUtils.rethrowRuntimeException(e);
				}
			}
		}
	}

	@Override
	public Throwable getRootCause(Throwable throwable) {
		// Overridden to support unwrapping of EvaluationExceptions.
		Assert.notNull(throwable, "Throwable must not be null");
		while (EXCEPTIONS_TO_UNWRAP.contains(throwable.getClass())) {
			throwable = throwable.getCause();
		}
		return throwable;
	}

	// FIXME combine with DefaultOriginalHandlerLocator ? How to config

	private static class DelegateDispatcherServlet extends DispatcherServlet {

		private static final long serialVersionUID = 1L;

		@Override
		public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
			return super.getHandler(request);
		}

		@Override
		public ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
				Object handler, Exception ex) throws Exception {
			return super.processHandlerException(request, response, handler, ex);
		}

		public View resolveViewId(String viewName, Locale locale) {
			try {
				return resolveViewName(viewName, null, locale, null);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

}

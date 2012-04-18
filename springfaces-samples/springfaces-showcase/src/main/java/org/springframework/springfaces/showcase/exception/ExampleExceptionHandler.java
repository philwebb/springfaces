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
package org.springframework.springfaces.showcase.exception;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.event.ExceptionQueuedEvent;

import org.springframework.util.Assert;

public class ExampleExceptionHandler extends ExceptionHandlerWrapper {

	private static Set<Class<?>> EXCEPTIONS_TO_UNWRAP;
	static {
		HashSet<Class<?>> unwrappedExceptions = new HashSet<Class<?>>();
		unwrappedExceptions.add(FacesException.class);
		unwrappedExceptions.add(ELException.class);
		unwrappedExceptions.add(EvaluationException.class);
		EXCEPTIONS_TO_UNWRAP = Collections.unmodifiableSet(unwrappedExceptions);
	}

	private ExceptionHandler wrapped;

	public ExampleExceptionHandler(ExceptionHandler wrapped) {
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
	public void xhandle() throws FacesException {
		Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
		while (iterator.hasNext()) {
			ExceptionQueuedEvent event = iterator.next();
			Throwable exception = event.getContext().getException();
			if (isExampleException(exception)) {
				FacesContext context = event.getContext().getContext();
				context.addMessage(null, new FacesMessage("Bad news"));
				context.renderResponse();
				iterator.remove();
				System.err.println("***************************************");
				event.getContext().getException().printStackTrace();
				throw (FacesException) event.getContext().getException();

				// // SpringFacesContext.getCurrentInstance().clearRendering();
				// UIViewRoot newRoot = context.getApplication().getViewHandler()
				// .createView(context, "/WEB-INF/pages/template/decorateall.xhtml");
				// context.setViewRoot(newRoot);

			}
		}
		super.handle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.ExceptionHandlerWrapper#handle()
	 */
	@Override
	public void handle() throws FacesException {
		// if (SpringFacesContext.getCurrentInstance() != null) {
		// SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		// WebApplicationContext context = springFacesContext.getWebApplicationContext();
		// Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
		// context, HandlerExceptionResolver.class, true, false);
		// ArrayList<HandlerExceptionResolver> handlerExceptionResolvers = new ArrayList<HandlerExceptionResolver>(
		// matchingBeans.values());
		// OrderComparator.sort(handlerExceptionResolvers);
		//
		// Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();
		// while (events.hasNext()) {
		// ExceptionQueuedEvent event = events.next();
		// ExceptionQueuedEventContext eventContext = event.getContext();
		// Throwable exception = getRootCause(eventContext.getException());
		// if (exception != null && exception instanceof Exception) {
		// HttpServletRequest request = (HttpServletRequest) eventContext.getContext().getExternalContext()
		// .getRequest();
		// HttpServletResponse response = (HttpServletResponse) eventContext.getContext().getExternalContext()
		// .getResponse();
		// Object handler = springFacesContext.getHandler();
		// for (HandlerExceptionResolver handlerExceptionResolver : handlerExceptionResolvers) {
		// ModelAndView modelAndView = handlerExceptionResolver.resolveException(request, response,
		// handler, (Exception) exception);
		// System.out.println(modelAndView);
		// if (modelAndView != null) {
		// events.remove();
		// break;
		// }
		// }
		// }
		// }
		// }
		super.handle();
	}

	@Override
	public Throwable getRootCause(Throwable throwable) {
		Assert.notNull(throwable, "Throwable must not be null");
		while (EXCEPTIONS_TO_UNWRAP.contains(throwable.getClass())) {
			throwable = throwable.getCause();
		}
		return throwable;
	}

	/**
	 * @param exception
	 * @return
	 */
	private boolean isExampleException(Throwable exception) {
		if (exception == null) {
			return false;
		}
		if (exception instanceof ExampleException) {
			return true;
		}
		return isExampleException(exception.getCause());
	}
}

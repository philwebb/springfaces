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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.el.EvaluationException;
import javax.faces.event.ExceptionQueuedEvent;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.exceptionhandler.ExceptionHandler;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * A JSF {@link ExceptionHandler} to integrate with Spring MVC by delegating to the specified {@link ExceptionHandler}s.
 * 
 * @author Phillip Webb
 */
@SuppressWarnings("deprecation")
public class MvcExceptionHandler extends ExceptionHandlerWrapper {

	private static Set<Class<?>> EXCEPTIONS_TO_UNWRAP;
	static {
		HashSet<Class<?>> unwrappedExceptions = new HashSet<Class<?>>();
		unwrappedExceptions.add(FacesException.class);
		unwrappedExceptions.add(ELException.class);
		unwrappedExceptions.add(EvaluationException.class);
		EXCEPTIONS_TO_UNWRAP = Collections.unmodifiableSet(unwrappedExceptions);
	}

	private javax.faces.context.ExceptionHandler wrapped;

	private Collection<? extends ExceptionHandler> exceptionHandlers;

	/**
	 * Create a new {@link MvcExceptionHandler} instance.
	 * @param wrapped the wrapped JSF exception handler
	 * @param exceptionHandlers a collection of Spring Faces exception handlers
	 */
	public MvcExceptionHandler(javax.faces.context.ExceptionHandler wrapped,
			Collection<? extends ExceptionHandler> exceptionHandlers) {
		Assert.notNull(wrapped, "Wrapped must not be null");
		this.wrapped = wrapped;
		this.exceptionHandlers = (exceptionHandlers == null ? Collections.<ExceptionHandler> emptyList()
				: exceptionHandlers);
	}

	@Override
	public javax.faces.context.ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		if (SpringFacesContext.getCurrentInstance() != null) {
			handle(SpringFacesContext.getCurrentInstance());
		}
		super.handle();
	}

	private void handle(SpringFacesContext context) {
		Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();
		while (events.hasNext()) {
			ExceptionQueuedEvent event = events.next();
			Throwable cause = getRootCause(event.getContext().getException());
			if (handle(context, cause)) {
				events.remove();
			}
		}
	}

	private boolean handle(SpringFacesContext context, Throwable cause) {
		for (ExceptionHandler exceptionHandler : this.exceptionHandlers) {
			try {
				if (exceptionHandler.handle(context, cause)) {
					return true;
				}
			} catch (Exception e) {
				ReflectionUtils.rethrowRuntimeException(e);
			}
		}
		return false;
	}

	@Override
	public Throwable getRootCause(Throwable throwable) {
		// Overridden to support unwrapping of EvaluationExceptions.
		Assert.notNull(throwable, "Throwable must not be null");
		while (EXCEPTIONS_TO_UNWRAP.contains(throwable.getClass())) {
			throwable = throwable.getCause();
		}
		return super.getRootCause(throwable);
	}
}

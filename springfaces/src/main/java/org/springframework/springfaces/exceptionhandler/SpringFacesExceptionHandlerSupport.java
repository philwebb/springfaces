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
package org.springframework.springfaces.exceptionhandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.el.EvaluationException;
import javax.faces.event.ExceptionQueuedEvent;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * {@link FacesWrapperFactory} for a JSF {@link javax.faces.context.ExceptionHandler} that offers extended Spring
 * support. All Spring Beans that implement {@link ExceptionHandler} with a matching generic type are considered. Beans
 * are ordered using {@link AnnotationAwareOrderComparator}, once the exception has been {@link ExceptionHandler#handle
 * handled} subsequent beans will not be called.
 * 
 * @author Phillip Webb
 */
@SuppressWarnings({ "deprecation", "rawtypes" })
public class SpringFacesExceptionHandlerSupport implements FacesWrapperFactory<javax.faces.context.ExceptionHandler>,
		ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

	private static Set<Class<?>> EXCEPTIONS_TO_UNWRAP;
	static {
		HashSet<Class<?>> unwrappedExceptions = new HashSet<Class<?>>();
		unwrappedExceptions.add(FacesException.class);
		unwrappedExceptions.add(ELException.class);
		unwrappedExceptions.add(EvaluationException.class);
		EXCEPTIONS_TO_UNWRAP = Collections.unmodifiableSet(unwrappedExceptions);
	}

	private ApplicationContext applicationContext;

	private List<ExceptionHandler> exceptionHandlers;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		if (applicationContext == this.applicationContext) {
			collectExceptionHandlerBeans();
		}
	}

	private void collectExceptionHandlerBeans() {
		this.exceptionHandlers = new ArrayList<ExceptionHandler>(BeanFactoryUtils.beansOfTypeIncludingAncestors(
				this.applicationContext, ExceptionHandler.class, true, true).values());
		Collections.sort(this.exceptionHandlers, new AnnotationAwareOrderComparator());
	}

	public javax.faces.context.ExceptionHandler newWrapper(Class<?> typeClass,
			javax.faces.context.ExceptionHandler wrapped) {
		return new SpringFacesExceptionHandler(wrapped);
	}

	protected class SpringFacesExceptionHandler extends ExceptionHandlerWrapper {

		private javax.faces.context.ExceptionHandler wrapped;

		/**
		 * Create a new {@link SpringFacesExceptionHandlerSupport} instance.
		 * @param wrapped the wrapped JSF exception handler
		 */
		public SpringFacesExceptionHandler(javax.faces.context.ExceptionHandler wrapped) {
			Assert.notNull(wrapped, "Wrapped must not be null");
			this.wrapped = wrapped;
		}

		@Override
		public javax.faces.context.ExceptionHandler getWrapped() {
			return this.wrapped;
		}

		@Override
		public void handle() throws FacesException {
			Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();
			while (events.hasNext()) {
				if (handle(events.next())) {
					events.remove();
				}
			}
			super.handle();
		}

		@SuppressWarnings("unchecked")
		private boolean handle(ExceptionQueuedEvent event) {
			Throwable exception = getRootCause(event.getContext().getException());
			Class<? extends Throwable> exceptionType = exception.getClass();
			for (ExceptionHandler handler : SpringFacesExceptionHandlerSupport.this.exceptionHandlers) {
				Class<?> handlerGeneric = GenericTypeResolver.resolveTypeArgument(handler.getClass(),
						ExceptionHandler.class);
				if (handlerGeneric == null || handlerGeneric.isAssignableFrom(exceptionType)) {
					try {
						if (handler.handle(exception, event)) {
							return true;
						}
					} catch (Exception e) {
						ReflectionUtils.rethrowRuntimeException(e);
					}
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
}

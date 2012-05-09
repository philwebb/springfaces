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

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Default implementation of {@link Dispatcher} that works identically to the Spring {@link DispatcherServlet}. Any
 * specific DispatcherServlet configuration in your <tt>web.xml</tt> should also be replicated here.
 * 
 * @author Phillip Webb
 */
public class DefaultDispatcher implements Dispatcher, ApplicationListener<ContextRefreshedEvent> {

	private Delegate delegate;

	/**
	 * Create a new {@link DefaultDispatcher} instance.
	 */
	public DefaultDispatcher() {
		this.delegate = createDelegate();
	}

	/**
	 * Factory method used to create the {@link Delegate}. Allows subclasses to work against different
	 * {@link DispatcherServlet} implementations if required.
	 * @return the delegate instance
	 */
	protected Delegate createDelegate() {
		return new DelegateDispatcherServlet();
	}

	public View resolveViewName(String viewName, Map<String, Object> model, Locale locale, HttpServletRequest request)
			throws Exception {
		return this.delegate.resolveViewName(viewName, model, locale, request);
	}

	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		return this.delegate.getHandler(request);
	}

	public ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {
		return this.delegate.processHandlerException(request, response, handler, ex);
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.delegate.onRefresh(event.getApplicationContext());
	}

	/**
	 * See {@link DispatcherServlet#setDetectAllHandlerMappings(boolean)} for details.
	 * @param detectAllHandlerMappings
	 * @see DispatcherServlet#setDetectAllHandlerMappings(boolean)
	 */
	public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
		this.delegate.setDetectAllHandlerMappings(detectAllHandlerMappings);
	}

	/**
	 * See {@link DispatcherServlet#setDetectAllHandlerAdapters(boolean)} for details.
	 * @param detectAllHandlerAdapters
	 * @see DispatcherServlet#setDetectAllHandlerAdapters(boolean)
	 */
	public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
		this.delegate.setDetectAllHandlerAdapters(detectAllHandlerAdapters);
	}

	/**
	 * See {@link DispatcherServlet#setDetectAllHandlerExceptionResolvers(boolean)} for details.
	 * @param detectAllHandlerExceptionResolvers
	 * @see DispatcherServlet#setDetectAllHandlerExceptionResolvers(boolean)
	 */
	public void setDetectAllHandlerExceptionResolvers(boolean detectAllHandlerExceptionResolvers) {
		this.delegate.setDetectAllHandlerExceptionResolvers(detectAllHandlerExceptionResolvers);
	}

	/**
	 * See {@link DispatcherServlet#setDetectAllViewResolvers(boolean)} for details.
	 * @param detectAllViewResolvers
	 * @see DispatcherServlet#setDetectAllViewResolvers(boolean)
	 */
	public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
		this.delegate.setDetectAllViewResolvers(detectAllViewResolvers);
	}

	/**
	 * See {@link DispatcherServlet#setCleanupAfterInclude(boolean)} for details.
	 * @param cleanupAfterInclude
	 * @see DispatcherServlet#setCleanupAfterInclude(boolean)
	 */
	public void setCleanupAfterInclude(boolean cleanupAfterInclude) {
		this.delegate.setCleanupAfterInclude(cleanupAfterInclude);
	}

	/**
	 * Delegate methods that should be exposed from a {@link DispatcherServlet} implementation.
	 */
	protected static interface Delegate {

		void onRefresh(ApplicationContext context);

		View resolveViewName(String viewName, Map<String, Object> model, Locale locale, HttpServletRequest request)
				throws Exception;

		ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, Object handler,
				Exception ex) throws Exception;

		HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

		void setDetectAllHandlerMappings(boolean detectAllHandlerMappings);

		void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters);

		void setDetectAllHandlerExceptionResolvers(boolean detectAllHandlerExceptionResolvers);

		void setDetectAllViewResolvers(boolean detectAllViewResolvers);

		void setCleanupAfterInclude(boolean cleanupAfterInclude);

	}

	/**
	 * {@link Delegate} implementation against a real {@link DispatcherServlet}.
	 */
	private static class DelegateDispatcherServlet extends DispatcherServlet implements Delegate {
		@Override
		public void onRefresh(ApplicationContext context) {
			super.onRefresh(context);
		}

		@Override
		public View resolveViewName(String viewName, Map<String, Object> model, Locale locale,
				HttpServletRequest request) throws Exception {
			return super.resolveViewName(viewName, model, locale, request);
		}

		@Override
		public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
			return super.getHandler(request);
		}

		@Override
		public ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
				Object handler, Exception ex) throws Exception {
			return super.processHandlerException(request, response, handler, ex);
		}
	}
}

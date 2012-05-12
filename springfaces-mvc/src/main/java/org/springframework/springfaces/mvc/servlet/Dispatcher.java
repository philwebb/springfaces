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

import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Dispatcher methods that are required when handling JSF requests.
 * 
 * @see DefaultDispatcher
 * 
 * @author Phillip Webb
 */
public interface Dispatcher {

	/**
	 * Resolve the given view name into a View object (to be rendered).
	 * @param viewName the name of the view to resolve
	 * @param model the model to be passed to the view
	 * @param locale the current locale
	 * @param request current HTTP servlet request
	 * @return the View object, or <tt>null</tt> if none found
	 * @throws Exception if the view cannot be resolved (typically in case of problems creating an actual View object)
	 */
	View resolveViewName(String viewName, Map<String, Object> model, Locale locale, HttpServletRequest request)
			throws Exception;

	/**
	 * Return the HandlerExecutionChain for this request.
	 * @param request current HTTP request
	 * @return the HandlerExecutionChain, or <tt>null</tt> if no handler could be found
	 * @throws Exception
	 */
	HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

	/**
	 * Determine an error ModelAndView via the registered HandlerExceptionResolvers.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or <tt>null</tt> if none chosen at the time of the exception (for example,
	 * if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution
	 * @return a corresponding ModelAndView
	 * @throws Exception if no error ModelAndView found
	 */
	ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception;
}

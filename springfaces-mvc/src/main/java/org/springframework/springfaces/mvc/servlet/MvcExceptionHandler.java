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

import javax.faces.context.ExternalContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.springfaces.exceptionhandler.ExceptionHandler;
import org.springframework.springfaces.exceptionhandler.ObjectMessageExceptionHandler;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.util.FacesUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.util.WebUtils;

/**
 * {@link ExceptionHandler} that delegates to a {@link Dispatcher} to allow exceptions to be handled using standard
 * Spring MVC semantics.
 * @author Phillip Webb
 */
public class MvcExceptionHandler implements ExceptionHandler<Exception>, Ordered, DispatcherAware {

	private int order = -2;

	private Dispatcher dispatcher;

	public boolean handle(Exception exception, ExceptionQueuedEvent event) throws Exception {
		SpringFacesContext context = SpringFacesContext.getCurrentInstance();
		if (context != null) {
			return handle(exception, context);
		}
		return false;
	}

	public boolean handle(Exception exception, SpringFacesContext context) throws Exception {
		ExternalContext externalContext = context.getFacesContext().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		Object handler = context.getHandler();
		ModelAndView modelAndView = processHandlerException(request, response, handler, exception);
		if (modelAndView != null) {
			WebUtils.clearErrorRequestAttributes(request);
			if (modelAndView.isReference()) {
				String viewName = modelAndView.getViewName();
				Map<String, Object> model = modelAndView.getModel();
				Locale locale = FacesUtils.getLocale(context.getFacesContext());
				View view = this.dispatcher.resolveViewName(viewName, model, locale, request);
				modelAndView.setView(view);
			}
			context.render(modelAndView.getView(), modelAndView.getModel());
			return true;
		}
		return false;
	}

	private ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception exception) throws Exception {
		try {
			return this.dispatcher.processHandlerException(request, response, handler, exception);
		} catch (Exception e) {
			if (e == exception) {
				return null;
			}
			throw e;
		}
	}

	public int getOrder() {
		return this.order;
	}

	/**
	 * Set the order. By default this class is executed before other {@link ExceptionHandler}s and before the
	 * {@link ObjectMessageExceptionHandler}.
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
}

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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.navigation.DestinationViewResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Default implementation of {@link DestinationViewResolver} that can resolves destinations to views in the same way as
 * the standard {@link DispatcherServlet}.
 * 
 * @author Phillip Webb
 */
public class DefaultDestinationViewResolver implements DestinationViewResolver, DispatcherAware {

	private Dispatcher dispatcher;

	public ModelAndView resolveDestination(FacesContext context, Object destination, Locale locale,
			SpringFacesModel model) throws Exception {
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		View view = this.dispatcher.resolveViewName(destination.toString(), model, locale, request);
		return new ModelAndView(view);
	}

	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
}

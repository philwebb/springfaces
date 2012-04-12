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

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;

/**
 * Default implementation of {@link OriginalHandlerLocator}.
 * 
 * @author Phillip Webb
 */
public class DefaultOriginalHandlerLocator implements OriginalHandlerLocator,
		ApplicationListener<ContextRefreshedEvent> {

	private DelegateDispatcherServlet delegate = new DelegateDispatcherServlet();

	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.delegate.onApplicationEvent(event);
	}

	public HandlerExecutionChain getOriginalHandler(HttpServletRequest request) throws Exception {
		return this.delegate.getHandler(request);
	}

	private static class DelegateDispatcherServlet extends DispatcherServlet {
		private static final long serialVersionUID = 1L;

		@Override
		public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
			return super.getHandler(request);
		}
	}

}

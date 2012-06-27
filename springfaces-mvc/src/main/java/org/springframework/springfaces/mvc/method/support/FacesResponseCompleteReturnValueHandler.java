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
package org.springframework.springfaces.mvc.method.support;

import javax.faces.context.FacesContext;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Decorator class that wraps {@link HandlerMethodReturnValueHandler}s to ensure that the current {@link FacesContext}
 * is marked as {@link FacesContext#responseComplete() responseComplete()} after the return value has been
 * {@link #handleReturnValue handled}.
 * @author Phillip Webb
 */
public class FacesResponseCompleteReturnValueHandler implements HandlerMethodReturnValueHandler {

	private HandlerMethodReturnValueHandler handler;

	/**
	 * Creates a new {@link FacesResponseCompleteReturnValueHandler} what wraps the specified handler to ensure that the
	 * {@link FacesContext#responseComplete()} is called after the return value has been handled.
	 * @param handler the delegate handler
	 */
	public FacesResponseCompleteReturnValueHandler(HandlerMethodReturnValueHandler handler) {
		Assert.notNull(handler, "Handler must not be null");
		this.handler = handler;
	}

	public boolean supportsReturnType(MethodParameter returnType) {
		return this.handler.supportsReturnType(returnType);
	}

	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		this.handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			context.responseComplete();
		}
	}
}

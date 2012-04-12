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

import org.springframework.web.servlet.HandlerExecutionChain;

/**
 * Strategy interface used by {@link FacesPostbackHandler} to obtain the handler that would have processed the request
 * if it were not a postback.
 * 
 * @see DefaultDestinationViewResolver
 * @author Phillip Webb
 */
public interface OriginalHandlerLocator {

	/**
	 * Return the handler that would have processed the request if it were not a postback.
	 * @param request the request that should be used to obtain the handler.
	 * @return the handler execution chain
	 * @throws Exception if the handler cannot be obtained
	 */
	HandlerExecutionChain getOriginalHandler(HttpServletRequest request) throws Exception;
}

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
package org.springframework.springfaces.mvc.navigation.requestmapped.filter;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Strategy interface that can be used to filter {@link MethodParameter}s.
 * @author Phillip Webb
 */
public interface MethodParameterFilter {

	/**
	 * Determine if the specified parameter matches.
	 * @param request the current native web request
	 * @param methodParameter the method parameter
	 * @return if the parameter matches
	 */
	boolean matches(NativeWebRequest request, MethodParameter methodParameter);
}

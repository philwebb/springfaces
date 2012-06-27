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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * {@link MethodParameterFilter} that filters items when they include any of the specified annotations.
 * @author Phillip Webb
 */
public class AnnotationMethodParameterFilter implements MethodParameterFilter {

	private Set<Class<?>> ignoredAnnotations;

	/**
	 * Create a new {@link AnnotationMethodParameterFilter}.
	 * @param ignoredAnnotations annotations to filter
	 */
	public AnnotationMethodParameterFilter(Class<?>... ignoredAnnotations) {
		Assert.notNull(ignoredAnnotations, "IgnoredAnnotations must not be null");
		this.ignoredAnnotations = new HashSet<Class<?>>(Arrays.asList(ignoredAnnotations));
	}

	public boolean matches(NativeWebRequest request, MethodParameter methodParameter) {
		for (Annotation ignoredAnnotation : methodParameter.getParameterAnnotations()) {
			if (this.ignoredAnnotations.contains(ignoredAnnotation.annotationType())) {
				return true;
			}
		}
		return false;
	}

}

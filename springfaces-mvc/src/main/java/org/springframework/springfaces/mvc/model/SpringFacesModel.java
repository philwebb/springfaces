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
package org.springframework.springfaces.mvc.model;

import java.util.Map;

import org.springframework.springfaces.mvc.expression.el.SpringFacesModelELResolver;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.util.Assert;

/**
 * The Model that relates to a Spring Faces MVC request.
 * @see SpringFacesModelELResolver
 * @author Phillip Webb
 */
public class SpringFacesModel extends ExtendedModelMap {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new empty Spring Faces Model.
	 */
	public SpringFacesModel() {
	}

	/**
	 * Create a new model containing elements from the specified source.
	 * @param source a map containing the initial model
	 */
	public SpringFacesModel(Map<String, ?> source) {
		Assert.notNull(source, "Source must not be null");
		putAll(source);
	}
}

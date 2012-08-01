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

import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * {@link HandlerMethodArgumentResolver} that can resolves items using the {@link SpringFacesModel}. This resolver
 * supports the following types:
 * <ul>
 * <li>{@link SpringFacesModel}</li>
 * <li>{@link ExtendedModelMap}</li>
 * <li>{@link ModelMap}</li>
 * <li>{@link Model}</li>
 * <li>{@link Map}</li>
 * </ul>
 * <p>
 * In addition in addition single values from the {@link SpringFacesModel} will be resolved when:
 * <ul>
 * <li>The item is not a {@link BeanUtils#isSimpleProperty simple} type</li>
 * <li>The is one and only one value in the model that can be used to resolve the parameter</li>
 * </ul>
 * If this behavior is not required set <tt>resolveModelItems</tt> to false then
 * {@link #SpringFacesModelMethodArgumentResolver(boolean) constructing} the resolver.
 * 
 * @author Phillip Webb
 */
public class SpringFacesModelMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private boolean resolveModelItems;

	/**
	 * Creates a new {@link SpringFacesModelMethodArgumentResolver}.
	 */
	public SpringFacesModelMethodArgumentResolver() {
		this(true);
	}

	/**
	 * Creates a new {@link SpringFacesModelMethodArgumentResolver}.
	 * 
	 * @param resolveModelItems determines if items from the model should be considered. See
	 * {@link SpringFacesModelMethodArgumentResolver class level} documentation for details.
	 */
	public SpringFacesModelMethodArgumentResolver(boolean resolveModelItems) {
		this.resolveModelItems = resolveModelItems;
	}

	public boolean supportsParameter(MethodParameter parameter) {
		SpringFacesModel model = getSpringFacesModel();
		if (model == null) {
			return false;
		}
		if (parameter.getParameterType().isInstance(model)) {
			return true;
		}
		return ((this.resolveModelItems) && (findModelValue(model, parameter) != null));
	}

	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		SpringFacesModel model = getSpringFacesModel();
		Assert.state(model != null, "Unable to obtain SpringFacesModel");
		if (parameter.getParameterType().isInstance(model)) {
			return model;
		}
		return findModelValue(model, parameter);
	}

	private Object findModelValue(SpringFacesModel model, MethodParameter parameter) {
		if (BeanUtils.isSimpleProperty(parameter.getParameterType())) {
			return null;
		}
		Object value = null;
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			if (parameter.getParameterType().isInstance(entry.getValue())) {
				if (value != null) {
					return null;
				}
				value = entry.getValue();
			}
		}
		return value;
	}

	private SpringFacesModel getSpringFacesModel() {
		FacesContext context = FacesContext.getCurrentInstance();
		return SpringFacesModelHolder.getModel(context == null ? null : context.getViewRoot());
	}
}

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
package org.springframework.springfaces.mvc.expression.el;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.expression.el.AbstractELResolver;
import org.springframework.springfaces.mvc.model.SpringFacesModel;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;

/**
 * Unified EL {@link ELResolver} that exposes values from the Spring Faces MVC model.
 * 
 * @see SpringFacesModel
 * 
 * @author Phillip Webb
 */
public class SpringFacesModelELResolver extends AbstractELResolver {

	@Override
	protected Object get(String property) {
		FacesContext context = FacesContext.getCurrentInstance();
		SpringFacesModel model = (context == null ? null : SpringFacesModelHolder.getModel(context.getViewRoot()));
		if (model != null) {
			return model.get(property);
		}
		return null;
	}
}

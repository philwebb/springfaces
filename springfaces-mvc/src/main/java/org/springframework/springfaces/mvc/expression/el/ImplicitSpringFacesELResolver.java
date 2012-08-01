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

import javax.el.CompositeELResolver;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.expression.el.AbstractELResolver;
import org.springframework.springfaces.expression.el.BeanBackedELResolver;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.model.SpringFacesModelHolder;

/**
 * Resolves "implicit" or well-known variables from SpringFaces MVC. The list of implicit flow variables consists of:
 * 
 * <pre>
 * handler
 * controller
 * model
 * </pre>
 * 
 * @author Phillip Webb
 */
public class ImplicitSpringFacesELResolver extends CompositeELResolver {

	public ImplicitSpringFacesELResolver() {
		add(new SpringFacesContextELResolver());
		add(new ModelELResolver());
	}

	/**
	 * Resolver for {@link SpringFacesContext} backed expressions.
	 */
	private static class SpringFacesContextELResolver extends BeanBackedELResolver {
		public SpringFacesContextELResolver() {
			map("handler");
			map("controller");
		}

		@Override
		protected Object getBean() {
			return SpringFacesContext.getCurrentInstance();
		}
	}

	private static class ModelELResolver extends AbstractELResolver {
		@Override
		protected Object get(String property) {
			if ("model".equals(property) && FacesContext.getCurrentInstance() != null) {
				FacesContext context = FacesContext.getCurrentInstance();
				return SpringFacesModelHolder.getModel(context.getViewRoot());
			}
			return null;
		}

	}
}

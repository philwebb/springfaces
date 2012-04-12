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
package org.springframework.springfaces.expression.el;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import org.springframework.beans.PropertyAccessor;
import org.springframework.expression.EvaluationContext;

/**
 * {@link PropertyAccessor} that allows SPEL expressions to access properties against the current {@link FacesContext}.
 * Properties will only be resolved if the {@link FacesContext} is available, at all other times this property accessor
 * will be ignored.
 * 
 * @author Phillip Webb
 */
public class FacesPropertyAccessor extends ELPropertyAccessor {

	@Override
	protected ELContext getElContext(EvaluationContext context, Object target) {
		if (FacesContext.getCurrentInstance() != null) {
			return FacesContext.getCurrentInstance().getELContext();
		}
		return null;
	}
}

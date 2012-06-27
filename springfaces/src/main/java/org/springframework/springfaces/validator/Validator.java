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
package org.springframework.springfaces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.springframework.springfaces.bean.ForClass;

/**
 * A variation of the JSF {@link javax.faces.validator.Validator} that support generic typing.
 * @see ForClass
 * @see SpringFacesValidatorSupport
 * @param <T> The type the validator is for
 * @author Phillip Webb
 */
public interface Validator<T> {

	/**
	 * See {@link javax.faces.validator.Validator#validate(FacesContext, UIComponent, Object)}.
	 * @param context the faces context
	 * @param component the source component
	 * @param value the value to validate
	 * @throws ValidatorException
	 */
	public void validate(FacesContext context, UIComponent component, T value) throws ValidatorException;

}

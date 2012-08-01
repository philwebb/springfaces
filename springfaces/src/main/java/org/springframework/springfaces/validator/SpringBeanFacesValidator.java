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

import javax.faces.FacesWrapper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.springframework.springfaces.component.SpringBeanPartialStateHolder;

/**
 * A JSF {@link javax.faces.validator.Validator} that delegates to a Spring Bean.
 * 
 * @author Phillip Webb
 */
public class SpringBeanFacesValidator extends SpringBeanPartialStateHolder<javax.faces.validator.Validator> implements
		javax.faces.validator.Validator, FacesWrapper<javax.faces.validator.Validator> {

	/**
	 * Constructor to satisfy the {@link StateHolder}. This constructor should not be used directly.
	 * @deprecated use alternative constructor
	 */
	@Deprecated
	public SpringBeanFacesValidator() {
		super();
	}

	/**
	 * Create a new {@link SpringBeanFacesValidator} instance.
	 * @param context the faces context
	 * @param beanName the bean name
	 */
	public SpringBeanFacesValidator(FacesContext context, String beanName) {
		super(context, beanName);
	}

	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		getBean().validate(context, component, value);
	}

	public javax.faces.validator.Validator getWrapped() {
		return getBean();
	}
}

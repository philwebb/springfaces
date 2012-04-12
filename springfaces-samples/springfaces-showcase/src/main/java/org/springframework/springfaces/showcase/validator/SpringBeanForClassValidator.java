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
package org.springframework.springfaces.showcase.validator;

import java.math.BigInteger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.springframework.springfaces.bean.ForClass;
import org.springframework.springfaces.validator.Validator;
import org.springframework.stereotype.Component;

@Component
@ForClass
public class SpringBeanForClassValidator implements Validator<BigInteger> {

	public void validate(FacesContext context, UIComponent component, BigInteger value) throws ValidatorException {
		if (value.intValue() < 30) {
			throw new ValidatorException(new FacesMessage("Value must be 30 or more"));
		}
	}

}

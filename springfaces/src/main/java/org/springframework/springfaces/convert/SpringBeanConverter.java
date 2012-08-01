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
package org.springframework.springfaces.convert;

import javax.faces.FacesWrapper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.component.SpringBeanPartialStateHolder;

/**
 * A JSF {@link javax.faces.convert.Converter} that delegates to a
 * {@link org.springframework.springfaces.convert.Converter} Spring Bean.
 * 
 * @author Phillip Webb
 * @param <T> the converted type
 */
public class SpringBeanConverter<T> extends
		SpringBeanPartialStateHolder<org.springframework.springfaces.convert.Converter<T>> implements
		javax.faces.convert.Converter, FacesWrapper<org.springframework.springfaces.convert.Converter<T>> {

	/**
	 * Constructor to satisfy the {@link StateHolder}. This constructor should not be used directly.
	 * @deprecated use alternative constructor
	 */
	@Deprecated
	public SpringBeanConverter() {
		super();
	}

	/**
	 * Create a new {@link SpringBeanConverter} instance.
	 * @param context the faces context
	 * @param beanName the bean name
	 */
	public SpringBeanConverter(FacesContext context, String beanName) {
		super(context, beanName);
	}

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return getBean().getAsObject(context, component, value);
	}

	@SuppressWarnings("unchecked")
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return getBean().getAsString(context, component, (T) value);
	}

	public org.springframework.springfaces.convert.Converter<T> getWrapped() {
		return getBean();
	}
}

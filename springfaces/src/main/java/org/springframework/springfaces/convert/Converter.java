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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.springframework.springfaces.bean.ForClass;

/**
 * A variation of the JSF {@link javax.faces.convert.Converter} that support generic typing.
 * @param <T> The type the converter is for
 * @see ForClass
 * @see SpringFacesConverterSupport
 * @author Phillip Webb
 */
public interface Converter<T> {

	/**
	 * See {@link javax.faces.convert.Converter#getAsObject(FacesContext, UIComponent, String)}.
	 * @param context the faces context
	 * @param component the component
	 * @param value the string value to convert
	 * @return the object value
	 * @throws ConverterException
	 */
	T getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException;

	/**
	 * See {@link javax.faces.convert.Converter#getAsString(FacesContext, UIComponent, Object)}.
	 * @param context the faces context
	 * @param component the component
	 * @param value the value to convert
	 * @return the string value
	 * @throws ConverterException
	 */
	String getAsString(FacesContext context, UIComponent component, T value) throws ConverterException;
}

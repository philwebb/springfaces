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
package org.springframework.springfaces.selectitems;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.springframework.springfaces.selectitems.ui.SelectItemsIterator;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * JSF {@link Converter} designed for use with {@link UISelectMany} or {@link UISelectOne} components. The
 * {@link #getAsString(FacesContext, UIComponent, Object) getAsString} method must be implemented by subclasses and must
 * provide a unique <tt>String</tt> for each {@link SelectItem#getValue() SelectItem value}. Unlike most
 * {@link Converter}s the {@link #getAsObject(FacesContext, UIComponent, String) getAsObject} method does not return a
 * new <tt>Object</tt> instance but instead returns the value from the matching parent component {@link SelectItem}.
 * 
 * @author Phillip Webb
 */
public abstract class SelectItemsConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		SelectItem matchingSelectItem = null;
		Iterator<SelectItem> iterator = getSelectItemsIterator(context, component);
		while (iterator.hasNext()) {
			SelectItem selectItem = iterator.next();
			String stringValue = getAsString(context, component, selectItem.getValue());
			if (ObjectUtils.nullSafeEquals(value, stringValue)) {
				Assert.state(matchingSelectItem == null, "Multiple select items mapped to string value '" + stringValue
						+ "' ensure that getAsString always returns a unique value");
				matchingSelectItem = selectItem;
			}
		}
		Assert.state(matchingSelectItem != null, "No select item mapped to string value '" + value
				+ "' ensure that getAsString always returns a consistent value");
		return matchingSelectItem.getValue();
	}

	/**
	 * Factory method used to provide an {@link Iterator} for the {@link SelectItem}s managed by the parent component.
	 * By default a {@link SelectItemsIterator} is returned.
	 * @param context the faces context
	 * @param component the component
	 * @return an {@link Iterator} of the {@link SelectItem}s managed by the <tt>component</tt>
	 */
	protected Iterator<SelectItem> getSelectItemsIterator(FacesContext context, UIComponent component) {
		return new SelectItemsIterator(context, component);
	}
}

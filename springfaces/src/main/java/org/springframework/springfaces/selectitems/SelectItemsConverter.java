package org.springframework.springfaces.selectitems;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.springframework.springfaces.selectitems.ui.SelectItemsIterator;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

//FIXME DC + Test
public abstract class SelectItemsConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Object objectValue = null;
		Iterator<SelectItem> iterator = getSelectItemsIterator(context, component);
		while (iterator.hasNext()) {
			SelectItem selectItem = iterator.next();
			String stringValue = getAsString(context, component, selectItem.getValue());
			if (ObjectUtils.nullSafeEquals(value, stringValue)) {
				Assert.state(objectValue == null, "Multiple select items mapped to string value '" + stringValue
						+ "' ensure getAsString always returns a unique value");
				objectValue = selectItem.getValue();
			}
		}
		return objectValue;
	}

	protected Iterator<SelectItem> getSelectItemsIterator(FacesContext context, UIComponent component) {
		return new SelectItemsIterator(context, component);
	}

}

package org.springframework.springfaces.showcase.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.convert.Converter;
import org.springframework.stereotype.Component;

@Component
public class GenericSpringBeanConverter implements Converter<ConvertedObject> {

	public ConvertedObject getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return new ConvertedObject("genericSpringBeanConverter", Long.valueOf(value));
	}

	public String getAsString(FacesContext context, UIComponent component, ConvertedObject value) {
		if (value == null) {
			return null;
		}
		return String.valueOf(value.getValue());
	}
}

package org.springframework.springfaces.showcase.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

/**
 * Example Faces Converter for {@link ConvertedObject} values.
 * 
 * @author Phillip Webb
 */
@Component
public class SpringBeanConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return new ConvertedObject("springBeanConverter", Long.valueOf(value));
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return ((ConvertedObject) value).getFrom();
	}

}

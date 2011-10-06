package org.springframework.springfaces.showcase.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.bean.ForClass;
import org.springframework.springfaces.convert.Converter;
import org.springframework.stereotype.Component;

/**
 * Example Faces Converter for {@link ConvertedObject} values.
 * 
 * @author Phillip Webb
 */
@Component
@ForClass
public class ForClassSpringConverter implements Converter<SpringConvertedObject> {

	public SpringConvertedObject getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return new SpringConvertedObject("forClassSpringConverter", Long.valueOf(value));
	}

	public String getAsString(FacesContext context, UIComponent component, SpringConvertedObject value) {
		if (value == null) {
			return null;
		}
		return value.getFrom();
	}
}

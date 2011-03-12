package org.springframework.springfaces.sample.controller;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class NameConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return new Name(0, value);
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return value.toString();
	}
}

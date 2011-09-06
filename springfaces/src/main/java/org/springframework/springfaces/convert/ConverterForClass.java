package org.springframework.springfaces.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public interface ConverterForClass<T> {

	T getAsObject(FacesContext context, UIComponent component, String value);

	String getAsString(FacesContext context, UIComponent component, T value);

}

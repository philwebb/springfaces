package org.springframework.springfaces.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class ObjectMessageConverter<T> implements Converter<T>, ConditionalConverterForClass {

	// FIXME implement

	public boolean isForClass(Class<?> targetClass) {
		// TODO Auto-generated method stub
		return false;
	}

	public T getAsObject(FacesContext context, UIComponent component, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component, T value) {
		// TODO Auto-generated method stub
		return null;
	}

}

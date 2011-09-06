package org.springframework.springfaces.convert;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * A variation of the JSF {@link Converter} that is for a specific class type. Beans registered within the Spring
 * context that implement this interface will be considered as the default converter for the class.
 * 
 * @param <T> The class that the converter is for.
 * @See {@link ConditionalConverterForClass}
 * 
 * @author Phillip Webb
 */
public interface ConverterForClass<T> {

	/**
	 * See {@link Converter#getAsObject(FacesContext, UIComponent, String)}.
	 * @param context the faces context
	 * @param component the component
	 * @param value the string value to convert
	 * @return the object value
	 */
	T getAsObject(FacesContext context, UIComponent component, String value);

	/**
	 * See {@link Converter#getAsString(FacesContext, UIComponent, Object)}.
	 * @param context the faces context
	 * @param component the component
	 * @param value the value to convert
	 * @return the string value
	 */
	String getAsString(FacesContext context, UIComponent component, T value);
}

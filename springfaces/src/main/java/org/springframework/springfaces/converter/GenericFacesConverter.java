package org.springframework.springfaces.converter;

import java.util.Collections;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.springfaces.context.SpringFacesContext;
import org.springframework.web.context.support.WebApplicationObjectSupport;

public class GenericFacesConverter extends WebApplicationObjectSupport implements ConditionalGenericConverter {

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, Object.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType.getType().getName().endsWith("Name")) {
			return true;
		}
		return false;
	}

	public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
		return SpringFacesContext.getCurrentInstance().doWithFacesContext(
				new SpringFacesContext.FacesContextCallback<Object>() {
					public Object doWithFacesContext(FacesContext facesContext) {
						Converter converter = facesContext.getApplication().createConverter(targetType.getType());
						if (converter == null) {
							return null;
						}
						UIComponent component = null;
						String value = (String) source;
						return converter.getAsObject(facesContext, component, value);
					}
				});
	}
}

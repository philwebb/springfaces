package org.springframework.springfaces.converter;

import java.util.Collections;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.springfaces.FacesHandlerInterceptor;
import org.springframework.springfaces.FacesHandlerInterceptor.FacesContextCallback;
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

		final Object[] rtn = new Object[1];
		rtn[0] = null;

		FacesHandlerInterceptor.getContext().doWithFacesContext(new FacesContextCallback() {
			public void doWith(FacesContext facesContext, Lifecycle lifecycle) {
				Converter converter = facesContext.getApplication().createConverter(targetType.getType());
				UIComponent component = null;
				String value = (String) source;
				rtn[0] = converter.getAsObject(facesContext, component, value);
			}
		});

		return rtn[0];
	}
}

package org.springframework.springfaces.mvc.converter;

import java.util.Collections;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationObjectSupport;

/**
 * A Spring {@link ConditionalGenericConverter Converter} that can convert <tt>String<tt>s to <tt>Object</tt>s by
 * delegating to JSF {@link Converter converters}. This converter will only be considered when a
 * {@link SpringFacesContext} is active. The {@link FacesConverterId @FacesConverterId} annotation can be used to
 * specify a specific JSF converter should be used, otherwise the converter will be created based on the source type.
 * 
 * @see FacesConverterId
 * 
 * @author Phillip Webb
 */
public class GenericFacesConverter extends WebApplicationObjectSupport implements ConditionalGenericConverter {

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, Object.class));
	}

	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (SpringFacesContext.getCurrentInstance() == null) {
			return false;
		}
		if (targetType.getAnnotation(FacesConverterId.class) != null) {
			return true;
		}
		FacesContext facesContext = SpringFacesContext.getCurrentInstance().getFacesContext();
		Application application = facesContext.getApplication();
		return ((application != null) && (application.createConverter(targetType.getType()) != null));
	}

	public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		FacesContext facesContext = springFacesContext.getFacesContext();
		try {
			Converter facesConverter = createFacesConverter(facesContext, targetType);
			return facesConverter.getAsObject(facesContext, null, source.toString());
		} finally {
			facesContext.release();
		}
	}

	private Converter createFacesConverter(FacesContext facesContext, TypeDescriptor targetType) {
		Application application = facesContext.getApplication();
		String converterId = getConverterId(targetType);
		if (converterId == null) {
			Converter converter = application.createConverter(targetType.getType());
			Assert.state(converter != null, "No JSF converter located for type " + targetType.getType());
			return converter;
		}
		Converter converter = application.createConverter(converterId);
		Assert.state(converter != null, "No JSF converter located for ID '" + converterId + "'");
		return converter;
	}

	private String getConverterId(TypeDescriptor targetType) {
		FacesConverterId annotation = (FacesConverterId) targetType.getAnnotation(FacesConverterId.class);
		if (annotation != null) {
			return annotation.value();
		}
		return null;
	}
}

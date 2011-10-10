package org.springframework.springfaces.convert;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.springframework.springfaces.bean.ConditionalForClass;
import org.springframework.springfaces.message.ObjectMessageSource;
import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;

/**
 * A {@link ConditionalForClass conditional} JSF {@link Converter} that converts objects to messages.
 * 
 * @author Phillip Webb
 */
public class ObjectMessageConverter implements Converter, ConditionalForClass {

	// FIXME this is fundamentally broken since output text does not call it. Probably needs to be pushed into
	// UIMessageSource

	private ObjectMessageSource messageSource;

	/**
	 * Create a new {@link ObjectMessageConverter} instance.
	 * @param messageSource the message source used to obtain messages
	 */
	public ObjectMessageConverter(ObjectMessageSource messageSource) {
		// FIXME consider change to setter
		Assert.notNull(messageSource, "MessageSource must not be null");
		this.messageSource = messageSource;
	}

	public boolean isForClass(Class<?> targetClass) {
		return messageSource.containsMessage(targetClass);
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		Locale locale = FacesUtils.getLocale(context);
		return messageSource.getMessage(value, locale);
	}

	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		throw new UnsupportedOperationException();
	}
}

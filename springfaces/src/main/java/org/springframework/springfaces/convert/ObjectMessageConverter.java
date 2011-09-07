package org.springframework.springfaces.convert;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.springfaces.util.FacesUtils;

public class ObjectMessageConverter<T> implements Converter<T>, ConditionalConverterForClass, MessageSourceAware {

	private static final Object[] NO_ARGUMENTS = {};

	private MessageSource messageSource;

	public boolean isForClass(Class<?> targetClass) {
		try {
			getMessage(getCode(targetClass));
			return true;
		} catch (NoSuchMessageException e) {
		}
		return false;
	}

	public String getAsString(FacesContext context, UIComponent component, T value) throws ConverterException {
		if (value == null) {
			return null;
		}
		String code = getCode(value.getClass());
		Locale locale = FacesUtils.getLocale(context);
		String message = getMessage(code, locale);
		return message;
	}

	public T getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getCode(Class<?> targetClass) {
		return targetClass.getName();
	}

	private String getMessage(String code) {
		return getMessage(code, null);
	}

	private String getMessage(String code, Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		String message = messageSource.getMessage(code, NO_ARGUMENTS, locale);
		// if useCodeAsDefaultMessage is set then an exception will not be thrown
		if (message.equals(code)) {
			throw new NoSuchMessageException(code);
		}
		return message;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}

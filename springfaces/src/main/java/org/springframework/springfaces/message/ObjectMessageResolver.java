package org.springframework.springfaces.message;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public class ObjectMessageResolver<T> implements MessageResolver<T> {

	private static final Object[] NO_ARGUMENTS = {};

	public boolean isResolvable(MessageSource messageSource, Class<?> objectType) {
		try {
			getMessage(messageSource, getCode(objectType));
			return true;
		} catch (NoSuchMessageException e) {
		}
		return false;
	}

	public String resolve(MessageSource messageSource, Locale locale, T object) {
		return resolve(messageSource, locale, null, object);
	};

	public String resolve(MessageSource messageSource, Locale locale, String option, T object) {
		if (object == null) {
			return null;
		}
		String code = getCode(object.getClass());
		String message = getMessage(messageSource, code, locale);
		return message;
	};

	protected String getCode(Class<?> objectType) {
		return objectType.getName();
	}

	private String getMessage(MessageSource messageSource, String code) {
		return getMessage(messageSource, code, null);
	}

	private String getMessage(MessageSource messageSource, String code, Locale locale) {
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
}

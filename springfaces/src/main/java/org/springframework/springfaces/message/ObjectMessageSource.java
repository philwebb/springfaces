package org.springframework.springfaces.message;

import java.util.Locale;

import org.springframework.context.NoSuchMessageException;

public interface ObjectMessageSource {

	boolean isSupported(Class<?> type);

	String getMessage(Object object, Locale locale) throws NoSuchMessageException;

}

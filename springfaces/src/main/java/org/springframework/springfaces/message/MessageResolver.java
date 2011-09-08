package org.springframework.springfaces.message;

import java.util.Locale;

import org.springframework.context.MessageSource;

public interface MessageResolver<T> {

	public boolean isResolvable(MessageSource messageSource, Class<?> type);

	public String resolve(MessageSource messageSource, Locale locale, T object);

	public String resolve(MessageSource messageSource, Locale locale, String option, T object);

}

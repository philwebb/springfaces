package org.springframework.springfaces.message;

import java.util.Locale;

import org.springframework.context.MessageSource;

public interface MessageResolver {

	boolean canResolveMessage(Class<?> type);

	String resolveMessage(Object object, Locale locale);

}

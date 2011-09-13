package org.springframework.springfaces.message.resolver;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public abstract class MessageSourceUtils {

	private static final Object[] NO_ARGUMENTS = {};

	public static boolean containsCode(MessageSource messageSource, String code) {
		return containsCode(messageSource, code, null);
	}

	public static boolean containsCode(MessageSource messageSource, String code, Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		try {
			String message = messageSource.getMessage(code, NO_ARGUMENTS, locale);
			// if useCodeAsDefaultMessage is set then an exception will not be thrown
			if (message.equals(code)) {
				return false;
			}
		} catch (NoSuchMessageException e) {
			return false;
		}
		return true;
	}

}

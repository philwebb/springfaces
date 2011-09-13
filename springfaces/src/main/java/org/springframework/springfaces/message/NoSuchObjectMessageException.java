package org.springframework.springfaces.message;

import java.util.Locale;

/**
 * Exception thrown when an object message can't be resolved.
 * 
 * @author Phillip Webb
 */
public class NoSuchObjectMessageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Object object;

	private Locale locale;

	/**
	 * Create a new {@link NoSuchObjectMessageException} instance.
	 * @param object the object that cannot be resolved
	 * @param locale the locale
	 */
	public NoSuchObjectMessageException(Object object, Locale locale) {
		this(object, locale, null);
	}

	/**
	 * Create a new {@link NoSuchObjectMessageException} instance.
	 * @param object the object that cannot be resolved
	 * @param locale the locale
	 * @param cause the root cause
	 */
	public NoSuchObjectMessageException(Object object, Locale locale, NoSuchObjectMessageException cause) {
		super("Unable to convert object of type " + object.getClass().getName() + " to a message for locale " + locale,
				cause);
		this.object = object;
		this.locale = locale;
	}

	/**
	 * Returns the object that was begin resolved.
	 * @return the object being resolved
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Returns the {@link Locale} being used when resolving the message.
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}
}

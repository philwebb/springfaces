/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	public NoSuchObjectMessageException(Object object, Locale locale, Throwable cause) {
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
		return this.object;
	}

	/**
	 * Returns the {@link Locale} being used when resolving the message.
	 * @return the locale
	 */
	public Locale getLocale() {
		return this.locale;
	}
}

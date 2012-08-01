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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Default implementation of {@link ObjectMessageSource} that delegates to a parent {@link MessageSource}. Object
 * messages are resolved using a generated {@link #resolveCode(Object, Locale) code}. T
 * <p>
 * Parameterized messages are supported by this resolver (see {@link #resolveMessage(Object, Object[], Locale)} for
 * details).
 * 
 * @author Phillip Webb
 */
public class DefaultObjectMessageSource extends DelegatingMessageSource implements ObjectMessageSource {

	private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{([\\w]+?)\\}");

	/**
	 * Create a new {@link DefaultObjectMessageSource} instance.
	 */
	public DefaultObjectMessageSource() {
	}

	/**
	 * Create a new {@link DefaultObjectMessageSource} instance with the specified parent.
	 * @param parent the parent message source.
	 */
	public DefaultObjectMessageSource(MessageSource parent) {
		Assert.notNull("Parent must not be null");
		setParentMessageSource(parent);
	}

	public String getMessage(Object object, Object[] args, Locale locale) throws NoSuchObjectMessageException {
		String message = getFullyResolvedMessage(object, args, locale, false);
		if (message == null && object != null) {
			throw new NoSuchObjectMessageException(object, locale);
		}
		return message;
	}

	/**
	 * Returns a fully resolved message, includes resolving any message parameters.
	 * @param object the object to resolve. Can be <tt>null</tt>
	 * @param args the message arguments
	 * @param locale the locale
	 * @param allowResolveToString if the {@link #resolveToString(Object)} can be used to create the result
	 * @return a fully resolved message
	 */
	private String getFullyResolvedMessage(Object object, Object[] args, Locale locale, boolean allowResolveToString) {
		if (object == null) {
			return null;
		}
		try {
			String resolvedMessage = resolveMessage(object, args, locale);
			if (resolvedMessage != null) {
				return expandParameters(resolvedMessage, object, args, locale);
			}
			if (allowResolveToString) {
				return resolveToString(object, args, locale);
			}
			return null;
		} catch (NoSuchObjectMessageException e) {
			throw new NoSuchObjectMessageException(object, locale, e);
		}
	}

	/**
	 * Resolve a message for the given object. The resolved message can include parameters of the form <tt>{name}</tt>
	 * that will be resolved using properties of the object. For example given following class: <code>
	 * public interface Name {
	 *   String getFirst();
	 *   String getLast();
	 * }
	 * </code> The message <tt>"Welcome back {first} {last}"</tt> could be used to construct a message containing the a
	 * persons full name.
	 * <p>
	 * By default this method will use the result of {@link #resolveCode(Object, Locale)} to obtain the message.
	 * 
	 * @param object the object to resolve (never <tt>null</tt>)
	 * @param args the message arguments
	 * @param locale the locale
	 * @return the resolved message or <tt>null</tt> if the object cannot be resolved
	 * @see #resolveToString(Object, Object[], Locale)
	 */
	protected String resolveMessage(Object object, Object[] args, Locale locale) {
		String code = resolveCode(object, locale);
		if (code != null) {
			try {
				String message = getMessage(code, args, locale);
				if (!code.equals(message)) {
					return message;
				}
			} catch (NoSuchMessageException e) {
			}
		}
		return null;
	}

	/**
	 * Resolve the message code for the given object. Objects that are {@link Enum} instances will use the fully
	 * qualified class name along with the enum name as the code. {@link Boolean} objects will use
	 * <tt>java.lang.Boolean.TRUE</tt> or <tt>java.lang.Boolean.FALSE</tt>. All other objects will use the fully
	 * qualified class name. Subclasses can override this method to support additional types.
	 * @param object the object to resolve (never <tt>null</tt>)
	 * @param locale the locale
	 * @return the message code for the object or <tt>null</tt> if the object cannot be resolved
	 */
	protected String resolveCode(Object object, Locale locale) {
		if (Boolean.class.isInstance(object)) {
			return object.getClass().getName() + "." + (((Boolean) object).booleanValue() ? "TRUE" : "FALSE");
		}
		if (Enum.class.isInstance(object)) {
			return object.getClass().getName() + "." + ((Enum<?>) object).name();
		}
		return object.getClass().getName();
	}

	/**
	 * Expand any parameters contained in the message by inspecting object properties.
	 * @param resolvedMessage the message to expand
	 * @param object the source object
	 * @param args the message arguments
	 * @param locale the locale
	 * @return a message with all supported parameters expanded
	 */
	private String expandParameters(String resolvedMessage, Object object, Object[] args, Locale locale) {
		Matcher matcher = PARAMETER_PATTERN.matcher(resolvedMessage);
		StringBuffer sb = new StringBuffer();
		BeanWrapper bean = new BeanWrapperImpl(object);
		while (matcher.find()) {
			String propertyName = matcher.group(1);
			if (bean.isReadableProperty(propertyName)) {
				Object propertyValue = bean.getPropertyValue(propertyName);
				String replacement = (propertyValue == null ? "" : getFullyResolvedMessage(propertyValue, args, locale,
						true));
				matcher.appendReplacement(sb, replacement);
			} else {
				// No property, leave the {variable} intact
				matcher.appendReplacement(sb, matcher.group(0));
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Resolve the given object to a <tt>String</tt> value. This method can be called if
	 * {@link #resolveMessage(Object, Object[], Locale)} returns <tt>null</tt>. By default the {@link Object#toString()
	 * toString()} method of the <tt>object</tt> will be used. Override this method if a specific string conversion
	 * strategy is required.
	 * @param object the object to resolve (never <tt>null</tt>)
	 * @param args the message arguments
	 * @param locale the locale
	 * @return the resolved string value.
	 */
	protected String resolveToString(Object object, Object[] args, Locale locale) {
		if (object.getClass().isArray()) {
			object = CollectionUtils.arrayToList(object);
		}
		if (object instanceof Collection) {
			List<String> resolvedCollection = new ArrayList<String>();
			for (Object element : (Collection<?>) object) {
				resolvedCollection.add(getFullyResolvedMessage(element, args, locale, true));
			}
			return StringUtils.collectionToCommaDelimitedString(resolvedCollection);
		}
		return object.toString();
	}
}

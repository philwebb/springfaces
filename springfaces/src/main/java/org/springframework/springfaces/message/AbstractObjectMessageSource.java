package org.springframework.springfaces.message;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Abstract implementation of the {@link ObjectMessageSource} interface. Subclasses must implement the
 * {@link #containsMessage(Class)} and {@link #resolveMessage(Object, Locale)} methods and can optionally override
 * {@link #resolveToString(Object)}. This class supports parameterized messaged (see
 * {@link #resolveMessage(Object, Locale)} for details.
 * 
 * @author Phillip Webb
 */
public abstract class AbstractObjectMessageSource implements ObjectMessageSource {

	private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{([\\w]+?)\\}");

	private Map<TypeAndLocale, Boolean> containsParameter;

	public AbstractObjectMessageSource() {
		reset();
	}

	public abstract boolean containsMessage(Class<?> type);

	public String getMessage(Object object, Locale locale) throws NoSuchMessageException {
		if (object == null) {
			return null;
		}
		Class<?> type = object.getClass();
		Assert.isTrue(containsMessage(type), "The object type " + type.getName() + " is not supported");
		return getFullyResolvedMessage(object, locale, false);
	}

	/**
	 * Reset internal state, clearing all caches. This method should be called if calls to
	 * {@link #resolveMessage(Object, Locale)} may now return a different result.
	 */
	protected void reset() {
		containsParameter = new HashMap<TypeAndLocale, Boolean>();
	}

	/**
	 * Returns a fully resolved message, includes resolving any message parameters.
	 * @param object the object to resolve. Can be <tt>null</tt>
	 * @param locale the locale
	 * @param allowResolveToString if the {@link #resolveToString(Object)} can be used to create the result
	 * @return a fully resolved message
	 */
	private String getFullyResolvedMessage(Object object, Locale locale, boolean allowResolveToString) {
		if (object == null) {
			return "";
		}
		String resolvedMessage = resolveMessage(object, locale);
		if (resolvedMessage == null && allowResolveToString) {
			resolvedMessage = resolveToString(object);
		}
		if (resolvedMessage == null) {
			throw new NoSuchObjectMessageException(object, locale);
		}
		try {
			return expandParameters(resolvedMessage, object, locale);
		} catch (NoSuchObjectMessageException e) {
			throw new NoSuchObjectMessageException(object, locale, e);
		}
	}

	/**
	 * Expand any parameters contained in the message by inspecting object properties.
	 * @param resolvedMessage the message to expand
	 * @param object the source object
	 * @param locale the locale
	 * @return a message with all supported parameters expanded
	 */
	private String expandParameters(String resolvedMessage, Object object, Locale locale) {

		TypeAndLocale typeAndLocale = new TypeAndLocale(object.getClass(), locale);

		Boolean containsParameters = this.containsParameter.get(typeAndLocale);
		if (Boolean.FALSE.equals(containsParameters)) {
			return resolvedMessage;
		}

		Matcher matcher = PARAMETER_PATTERN.matcher(resolvedMessage);
		StringBuffer sb = new StringBuffer();
		BeanWrapper bean = new BeanWrapperImpl(object);
		boolean foundMatch = false;
		while (matcher.find()) {
			foundMatch = true;
			String propertyName = matcher.group(1);
			if (bean.isReadableProperty(propertyName)) {
				Object propertyValue = bean.getPropertyValue(propertyName);
				String replacement = getFullyResolvedMessage(propertyValue, locale, true);
				matcher.appendReplacement(sb, replacement);
			} else {
				// No property, leave the {variable} intact
				matcher.appendReplacement(sb, matcher.group(0));
			}
		}
		matcher.appendTail(sb);

		if (containsParameters == null) {
			this.containsParameter.put(typeAndLocale, foundMatch);
		}

		return sb.toString();
	}

	/**
	 * Resolve a message for the given object. The resolved message can include parameters of the form <tt>{name}</tt>
	 * that will be resolved using properties of the object. For example given following class: <code>
	 * public interface Name {
	 *   String getFirst();
	 *   String getLast();
	 * }
	 * </code> The message <tt>"Welcome back {first} {last}"</tt> could be used to construct a message containing the a
	 * persons full name,
	 * 
	 * @param object the object to resolve (never <tt>null</tt>)
	 * @param locale the locale
	 * @return the resolved message or <tt>null</tt> if the object cannot be resolved
	 * @see #resolveToString(Object)
	 */
	protected abstract String resolveMessage(Object object, Locale locale);

	/**
	 * Resolve the given object to a <tt>String</tt> value. This method can be called if
	 * {@link #resolveMessage(Object, Locale)} returns <tt>null</tt>. By default the {@link Object#toString()
	 * toString()} method of the <tt>object</tt> will be used. Override this method if a specific string conversion
	 * strategy is required.
	 * @param object the object to resolve (never <tt>null</tt>)
	 * @return the resolved string value.
	 */
	protected String resolveToString(Object object) {
		return object.toString();
	}

	/**
	 * Holder for a type and locale. Used as a Mao key.
	 */
	private static class TypeAndLocale {
		private Class<?> type;
		private Locale locale;

		public TypeAndLocale(Class<?> type, Locale locale) {
			this.type = type;
			this.locale = locale;
		}

		@Override
		public int hashCode() {
			return ObjectUtils.nullSafeHashCode(locale) + ObjectUtils.nullSafeHashCode(type);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			TypeAndLocale other = (TypeAndLocale) obj;
			return ObjectUtils.nullSafeEquals(type, other.type) && ObjectUtils.nullSafeEquals(locale, other.locale);
		}
	}
}

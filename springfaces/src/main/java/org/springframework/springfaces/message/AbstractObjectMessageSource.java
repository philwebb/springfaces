package org.springframework.springfaces.message;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;

public abstract class AbstractObjectMessageSource implements ObjectMessageSource {

	private static final Pattern PATTERN = Pattern.compile("\\{([\\w]+?)\\}");

	public abstract boolean isSupported(Class<?> type);

	public String getMessage(Object object, Locale locale) throws NoSuchMessageException {
		if (object == null) {
			return null;
		}
		Class<?> type = object.getClass();
		Assert.isTrue(isSupported(type), "The object type " + type.getName() + " is not supported");
		return getFullyResolvedMessage(object, locale, false);
	}

	private String getFullyResolvedMessage(Object object, Locale locale, boolean allowToString) {
		if (object == null) {
			return "";
		}
		String resolvedMessage = resolveMessage(object, locale);
		if (resolvedMessage == null && allowToString) {
			return toString(object);
		}
		Assert.state(resolvedMessage != null, "Unable to resolve message from object type "
				+ object.getClass().getName() + " using locale " + locale);
		return expandParamters(resolvedMessage, object, locale);
	}

	private String expandParamters(String resolvedMessage, Object object, Locale locale) {
		Matcher matcher = PATTERN.matcher(resolvedMessage);
		StringBuffer sb = new StringBuffer();
		BeanWrapper bean = new BeanWrapperImpl(object);
		while (matcher.find()) {
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
		return sb.toString();
	}

	protected abstract String resolveMessage(Object object, Locale locale);

	protected String toString(Object object) {
		return object.toString();
	}
}

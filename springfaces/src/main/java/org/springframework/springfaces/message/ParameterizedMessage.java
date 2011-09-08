package org.springframework.springfaces.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

/**
 * A message that includes additional resolvable parameters. Parameters are marked using curly braces, for example,
 * <tt>"welcome {name}"</tt>. Use the {@link #get(String)} method to obtain a {@link ParameterizedMessage} instance.
 * 
 * @see #get(String)
 * @see #resolve(ParamaterResolver)
 * 
 * @author Phillip Webb
 */
class ParameterizedMessage {

	private static final Pattern PATTERN = Pattern.compile("\\{([\\w]+?)\\}");

	private static final ParameterizedMessage NONE = new ParameterizedMessage("");

	private static Map<String, ParameterizedMessage> cache = new ConcurrentHashMap<String, ParameterizedMessage>();

	/**
	 * Sections of the message. This list contains either {@link Parameter}s or <tt>String</tt>s.
	 */
	private List<Object> sections = new ArrayList<Object>();

	/**
	 * Private constructor for parameterized messages.
	 * @param matcher The matcher used to build the message
	 * @see #get(String)
	 */
	private ParameterizedMessage(Matcher matcher) {
		matcher.reset();
		StringBuffer variable = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			sb.setLength(0);
			variable.setLength(0);
			variable.append(matcher.group(0).substring(1));
			variable.setLength(variable.length() - 1);
			matcher.appendReplacement(sb, "");
			sections.add(sb.toString());
			sections.add(new Parameter(variable.toString()));
		}
		sb.setLength(0);
		matcher.appendTail(sb);
		sections.add(sb.toString());
	}

	/**
	 * Private constructor for non-parameterized messages.
	 * @param nonParameterizedMessage The message
	 * @see #get(String)
	 */
	private ParameterizedMessage(String nonParameterizedMessage) {
		this.sections = Collections.<Object> singletonList(nonParameterizedMessage);
	}

	/**
	 * Returns a fully resolved message. The resolver will be used to obtain parameter values. If any parameter resolves
	 * to <tt>null</tt> the original placeholder syntax is retained.
	 * @param resolver a parameter resolver
	 * @return the completely resolved message.
	 */
	public String resolve(ParamaterResolver resolver) {
		Assert.notNull(resolver, "Resolver must not be null");
		StringBuffer sb = new StringBuffer();
		for (Object section : sections) {
			sb.append(section instanceof Parameter ? ((Parameter) section).resolve(resolver) : section.toString());
		}
		return sb.toString();
	}

	/**
	 * Determines if the specified message contains any parameter markers.
	 * @param message the message (can be <tt>null</tt>)
	 * @return <tt>true</tt> if the message contains curly brace parameter markers
	 * @see #get(String)
	 */
	public static boolean isParameterized(String message) {
		return (getIfParameterized(message) != null);
	}

	/**
	 * Returns a {@link ParameterizedMessage} for the given message. It is recommended that the
	 * {@link #isParameterized(String)} method is used to determine if obtaining a {@link ParameterizedMessage} is
	 * necessary as this method will never return <tt>null</tt>.
	 * @param message the message (must not be <tt>null</tt>)
	 * @return A {@link ParameterizedMessage} instance
	 * @see #isParameterized(String)
	 */
	public static ParameterizedMessage get(String message) {
		Assert.notNull(message, "Message must not be null");
		ParameterizedMessage parameterizedMessage = getIfParameterized(message);
		if (parameterizedMessage == null) {
			parameterizedMessage = new ParameterizedMessage(message);
		}
		return parameterizedMessage;
	}

	/**
	 * Gets a {@link ParameterizedMessage} or returns <tt>null</tt> if the message does not contain parameters.
	 * @param message the message (can be <tt>null</tt>)
	 * @return A {@link ParameterizedMessage} or <tt>null</tt>
	 */
	private static ParameterizedMessage getIfParameterized(String message) {
		if (message == null) {
			return null;
		}
		ParameterizedMessage parameterizedMessage = cache.get(message);
		if (parameterizedMessage == null) {
			parameterizedMessage = NONE;
			Matcher matcher = PATTERN.matcher(message);
			if (matcher.find()) {
				parameterizedMessage = new ParameterizedMessage(matcher);
			}
			cache.put(message, parameterizedMessage);
		}
		return (parameterizedMessage == NONE ? null : parameterizedMessage);
	}

	/**
	 * Resolves a single parameter from a {@link ParameterizedMessage}.
	 */
	public static interface ParamaterResolver {
		public String resolve(String parameter);
	}

	/**
	 * A single parameter from the message.
	 */
	private static class Parameter {

		private String name;

		public Parameter(String name) {
			this.name = name;
		}

		public String resolve(ParamaterResolver resolver) {
			String resolved = resolver.resolve(name);
			if (resolved == null) {
				return "{" + name + "}";
			}
			return resolved;
		}
	}
}

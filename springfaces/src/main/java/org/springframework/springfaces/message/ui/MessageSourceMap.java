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
package org.springframework.springfaces.message.ui;

import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.style.ToStringCreator;
import org.springframework.springfaces.message.NoSuchObjectMessageException;
import org.springframework.springfaces.message.ObjectMessageSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Exposes a {@link MessageSource} as a read-only {@link Map} allowing EL expressions to easily resolve messages.
 * Messages can accessed using expressions of the form 'messageSourceMap.messageCode'. For example, assuming the
 * variable <tt>'messages'</tt> is the <tt>MessageSourceMap</tt> the expression <tt>#{messages.aboutthesite}</tt> will
 * resolve the message code <tt>'aboutthesite'</tt>.
 * <p>
 * Messages parameters can also be resolved by chaining arguments. For example a welcome message
 * <tt>'welcome=Welcome {0} {1}'</tt> can be resolved using a <tt>person</tt> bean with the expression
 * <tt>#{messages.welcome[person.firstName][person.secondName]}</tt>.
 * <p>
 * Objects can also be resolved directly when the <tt>messageSource</tt> is an {@link ObjectMessageSource}. For example
 * <tt>#{messages[someObject]}</tt> will resolve <tt>someObject</tt> using the
 * {@link ObjectMessageSource#getMessage(Object, Object[], Locale)} method.
 * <p>
 * NOTE: Only the {@link #get(Object) get} method can be used on this {@link Map}, all other calls will throw an
 * {@link UnsupportedOperationException}.
 * 
 * @author Phillip Webb
 * @author Pedro Casagrande
 */
public class MessageSourceMap extends AbstractMap<Object, Object> {

	private static final Object[] NO_ARGUMENTS = {};

	private static final String[] NO_PREFIX_CODES = {};

	private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{([\\w]+?)\\}");

	/**
	 * The message source used to resolve messages.
	 */
	private MessageSource messageSource;

	/**
	 * The prefix codes.
	 */
	private String[] prefixCodes;

	/**
	 * Create a new MessageSourceMap
	 * @param messageSource a non-null message source
	 */
	public MessageSourceMap(MessageSource messageSource) {
		this(messageSource, null);
	}

	/**
	 * Create a new MessageSourceMap
	 * @param messageSource a non-null message source
	 * @param prefixCodes prefixes that should be applied to codes being resolved or <tt>null</tt> if no prefixes are
	 * required. Prefixes should be specified in the order that they are tried
	 */
	public MessageSourceMap(MessageSource messageSource, String[] prefixCodes) {
		Assert.notNull(messageSource, "MessageSource must not be null");
		this.messageSource = messageSource;
		this.prefixCodes = (prefixCodes == null ? NO_PREFIX_CODES : prefixCodes);
	}

	/**
	 * Returns the locale that should be used when resolving messages.
	 * @return The locale
	 */
	protected Locale getLocale() {
		return null;
	}

	/**
	 * Resolve a single message argument to render as part of the message.
	 * @param argument the argument to resolve
	 * @return the resolved argument
	 */
	protected Object resolveMessageArgument(Object argument) {
		if (this.messageSource instanceof ObjectMessageSource) {
			try {
				return ((ObjectMessageSource) this.messageSource).getMessage(argument, NO_ARGUMENTS, getLocale());
			} catch (NoSuchObjectMessageException e) {
			}
		}
		return argument;
	}

	/**
	 * Called to handle any {@link NoSuchMessageException} exceptions. The default behavior throws the exception,
	 * subclasses can override to handle exception differently.
	 * @param resolvable The message resolvable
	 * @param exception the exception to handle
	 */
	protected void handleNoSuchMessageException(MessageSourceResolvable resolvable, NoSuchMessageException exception) {
		throw exception;
	}

	/**
	 * Indicates if the map should try and deduce when to return a <tt>String</tt> and when to return a nested
	 * <tt>Map</tt>. This feature can be useful when component assume messages are always <tt>Strings</tt>.
	 * @return <tt>true</tt> if strings should be returned when possible
	 */
	protected boolean returnStringsWhenPossible() {
		return false;
	}

	@Override
	public Object get(Object key) {
		if (key == null) {
			return null;
		}
		if (key instanceof String) {
			return new MessageCodeValue((String) key, NO_ARGUMENTS).getReturnValue();
		}
		if (this.messageSource instanceof ObjectMessageSource) {
			return new ObjectMessageValue(key, NO_ARGUMENTS).getReturnValue();
		}
		throw new IllegalArgumentException("Unable to resolve " + key.getClass().getName()
				+ " messages when not using an ObjectMessageSource.");
	}

	/**
	 * Convenience method that can be used to resolve messages parameters with the specified chained arguments.
	 * @param keys the keys to resolve
	 * @return the message value
	 */
	@SuppressWarnings("unchecked")
	public Object get(Object... keys) {
		Object source = this;
		for (Object key : keys) {
			Assert.isInstanceOf(Map.class, source);
			source = ((Map<Object, Object>) source).get(key);
		}
		return source;
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("messageSource", this.messageSource)
				.append("prefixCodes", this.prefixCodes).toString();
	}

	private abstract class AbstractValue extends AbstractMap<Object, Object> {

		private Object[] arguments;

		public AbstractValue(Object[] arguments) {
			this.arguments = arguments;
		}

		@Override
		public Set<java.util.Map.Entry<Object, Object>> entrySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object get(Object key) {
			Object[] childArguments = new Object[this.arguments.length + 1];
			System.arraycopy(this.arguments, 0, childArguments, 0, this.arguments.length);
			childArguments[childArguments.length - 1] = resolveMessageArgument(key);
			return createNestedValue(childArguments).getReturnValue();
		}

		protected Object[] getArguments() {
			return this.arguments;
		}

		protected abstract AbstractValue createNestedValue(Object[] arguments);

		@Override
		public abstract String toString();

		/**
		 * Returns the message with placeholder elements intact.
		 * @return the message string or <tt>null</tt> if no message is found.
		 */
		protected abstract String toStringWithPlaceholders();

		/**
		 * Returns the appropriate return value (either a <tt>String</tt> or <tt>this</tt>) depending on the result of
		 * {@link MessageSourceMap#returnStringsWhenPossible()} and if the correct number of arguments have been
		 * supplied.
		 * @return the return value
		 */
		public Object getReturnValue() {
			if (returnStringsWhenPossible() && hasCorrentNumberOfArguments()) {
				return toString();
			}
			return this;
		}

		private boolean hasCorrentNumberOfArguments() {
			String messageWithPlaceHolders = toStringWithPlaceholders();
			if (!StringUtils.hasLength(messageWithPlaceHolders)) {
				return getArguments().length == 0;
			}
			Matcher matcher = PARAMETER_PATTERN.matcher(messageWithPlaceHolders);
			int numberOfParameters = 0;
			while (matcher.find()) {
				numberOfParameters++;
			}
			return getArguments().length == numberOfParameters;
		}
	}

	/**
	 * Internal implementation of {@link MessageSourceResolvable}.
	 */
	private class MessageCodeValue extends AbstractValue implements MessageSourceResolvable {

		private String code;
		private String[] codes;

		public MessageCodeValue(String code, Object[] arguments) {
			super(arguments);
			this.code = code;
			this.codes = buildPrefixedCodes(code);
		}

		private String[] buildPrefixedCodes(String code) {
			if (MessageSourceMap.this.prefixCodes.length == 0) {
				return new String[] { code };
			}
			String[] codes = new String[MessageSourceMap.this.prefixCodes.length];
			System.arraycopy(MessageSourceMap.this.prefixCodes, 0, codes, 0, MessageSourceMap.this.prefixCodes.length);
			for (int i = 0; i < codes.length; i++) {
				codes[i] = codes[i] == null ? code : codes[i].concat(code);
			}
			return codes;
		}

		@Override
		protected AbstractValue createNestedValue(Object[] childArguments) {
			return new MessageCodeValue(this.code, childArguments);
		}

		public String[] getCodes() {
			return this.codes;
		}

		@Override
		public Object[] getArguments() {
			return super.getArguments();
		}

		public String getDefaultMessage() {
			return null;
		}

		@Override
		public String toString() {
			try {
				return MessageSourceMap.this.messageSource.getMessage(this, getLocale());
			} catch (NoSuchMessageException e) {
				handleNoSuchMessageException(this, e);
				return this.code;
			}
		}

		@Override
		protected String toStringWithPlaceholders() {
			try {
				return MessageSourceMap.this.messageSource.getMessage(getMessageSourceResolvableWithoutArguments(),
						getLocale());
			} catch (NoSuchMessageException e) {
				return null;
			}
		}

		private MessageSourceResolvable getMessageSourceResolvableWithoutArguments() {
			return new MessageSourceResolvable() {

				public String getDefaultMessage() {
					return null;
				}

				public String[] getCodes() {
					return MessageCodeValue.this.getCodes();
				}

				public Object[] getArguments() {
					return NO_ARGUMENTS;
				}
			};
		}
	}

	private class ObjectMessageValue extends AbstractValue {

		private Object object;

		public ObjectMessageValue(Object object, Object[] arguments) {
			super(arguments);
			this.object = object;
		}

		@Override
		protected AbstractValue createNestedValue(Object[] childArguments) {
			return new ObjectMessageValue(this.object, childArguments);
		}

		@Override
		public String toString() {
			try {
				return ((ObjectMessageSource) MessageSourceMap.this.messageSource).getMessage(this.object,
						getArguments(), getLocale());
			} catch (NoSuchObjectMessageException e) {
				return String.valueOf(this.object);
			}
		}

		@Override
		protected String toStringWithPlaceholders() {
			try {
				return ((ObjectMessageSource) MessageSourceMap.this.messageSource).getMessage(this.object,
						NO_ARGUMENTS, getLocale());
			} catch (NoSuchObjectMessageException e) {
				return null;
			}
		}
	}
}

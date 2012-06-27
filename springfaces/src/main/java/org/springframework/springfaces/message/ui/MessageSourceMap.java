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

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.style.ToStringCreator;
import org.springframework.springfaces.message.NoSuchObjectMessageException;
import org.springframework.springfaces.message.ObjectMessageSource;
import org.springframework.springfaces.message.ui.MessageSourceMap.Value;
import org.springframework.util.Assert;

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
 * Objects returned from the map can be displayed using <tt>toString()</tt>.
 * <p>
 * Objects can also be resolved directly when the <tt>messageSource</tt> is an {@link ObjectMessageSource}. For example
 * <tt>#{messages[someObject]}</tt> will resolve <tt>someObject</tt> using the
 * {@link ObjectMessageSource#getMessage(Object, Object[], Locale)} method.
 * <p>
 * NOTE: Only the {@link #get(Object)} method can be used on this {@link Map}. All other calls will throw an
 * {@link UnsupportedOperationException}.
 * @author Phillip Webb
 */
public class MessageSourceMap extends AbstractMap<Object, Value> {

	private static final Object[] NO_ARGUMENTS = {};

	private static final String[] NO_PREFIX_CODES = {};

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
	 * @param exception the exception to handle
	 */
	protected void handleNoSuchMessageException(NoSuchMessageException exception) {
		throw exception;
	}

	@Override
	public Value get(Object key) {
		if (key == null) {
			return null;
		}
		if (key instanceof String) {
			return new MessageCodeValue((String) key, NO_ARGUMENTS);
		}
		if (this.messageSource instanceof ObjectMessageSource) {
			return new ObjectMessageValue(key, NO_ARGUMENTS);
		}
		throw new IllegalArgumentException("Unable to resolve " + key.getClass().getName()
				+ " messages when not using an ObjectMessageSource.");
	}

	@Override
	public Set<Map.Entry<Object, Value>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("messageSource", this.messageSource)
				.append("prefixCodes", this.prefixCodes).toString();
	}

	/**
	 * A Value contained within the {@link MessageSourceMap}. Values are both themselves {@link Map}s and
	 * {@link MessageSourceResolvable}.
	 */
	public static interface Value extends Map<Object, Value> {
	}

	private abstract class AbstractValue extends AbstractMap<Object, Value> implements Value {

		private Object[] arguments;

		public AbstractValue(Object[] arguments) {
			this.arguments = arguments;
		}

		@Override
		public Set<java.util.Map.Entry<Object, Value>> entrySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Value get(Object key) {
			Object[] childArguments = new Object[this.arguments.length + 1];
			System.arraycopy(this.arguments, 0, childArguments, 0, this.arguments.length);
			childArguments[childArguments.length - 1] = resolveMessageArgument(key);
			return createNestedValue(childArguments);
		}

		protected abstract Value createNestedValue(Object[] arguments);

		protected Object[] getArguments() {
			return this.arguments;
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
		protected Value createNestedValue(Object[] childArguments) {
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
				handleNoSuchMessageException(e);
				return this.code;
			}
		}

	}

	private class ObjectMessageValue extends AbstractValue {

		private Object object;

		public ObjectMessageValue(Object object, Object[] arguments) {
			super(arguments);
			this.object = object;
		}

		@Override
		protected Value createNestedValue(Object[] childArguments) {
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
	}
}

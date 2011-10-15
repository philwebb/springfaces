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
 * 
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
		if (messageSource instanceof ObjectMessageSource) {
			try {
				return ((ObjectMessageSource) messageSource).getMessage(argument, NO_ARGUMENTS, getLocale());
			} catch (NoSuchObjectMessageException e) {
			}
		}
		return argument;
	}

	/**
	 * Called to handle any {@link NoSuchMessageException} exceptions. The default behaviour throws the exception,
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
		if (messageSource instanceof ObjectMessageSource) {
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
		return new ToStringCreator(this).append("messageSource", messageSource).append("prefixCodes", prefixCodes)
				.toString();
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
			Object[] childArguments = new Object[arguments.length + 1];
			System.arraycopy(arguments, 0, childArguments, 0, arguments.length);
			childArguments[childArguments.length - 1] = resolveMessageArgument(key);
			return createNestedValue(childArguments);
		}

		protected abstract Value createNestedValue(Object[] arguments);

		protected Object[] getArguments() {
			return arguments;
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
			if (prefixCodes.length == 0) {
				return new String[] { code };
			}
			String[] codes = new String[prefixCodes.length];
			System.arraycopy(prefixCodes, 0, codes, 0, prefixCodes.length);
			for (int i = 0; i < codes.length; i++) {
				codes[i] = codes[i] == null ? code : codes[i].concat(code);
			}
			return codes;
		}

		@Override
		protected Value createNestedValue(Object[] childArguments) {
			return new MessageCodeValue(code, childArguments);
		}

		public String[] getCodes() {
			return codes;
		}

		public Object[] getArguments() {
			return super.getArguments();
		}

		public String getDefaultMessage() {
			return null;
		}

		@Override
		public String toString() {
			try {
				return messageSource.getMessage(this, getLocale());
			} catch (NoSuchMessageException e) {
				handleNoSuchMessageException(e);
				return code;
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
			return new ObjectMessageValue(object, childArguments);
		}

		@Override
		public String toString() {
			try {
				return ((ObjectMessageSource) messageSource).getMessage(object, getArguments(), getLocale());
			} catch (NoSuchMessageException e) {
				handleNoSuchMessageException(e);
				return String.valueOf(object);
			}
		}
	}
}

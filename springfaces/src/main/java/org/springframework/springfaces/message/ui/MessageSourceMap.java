package org.springframework.springfaces.message.ui;

import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.style.ToStringCreator;
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
 * Objects returned from the map can be displayed using <tt>toString()</tt> they can also be cast to
 * {@link MessageSourceResolvable} if necessary.
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
	 * Resolve a single message argument to render as part of the message. The default behaviour returns the argument
	 * unchanged, subclasses can override this method as required.
	 * @param argument the argument to resolve
	 * @return the resolved argument
	 */
	protected Object resolveMessageArgument(Object argument) {
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
		Assert.state(key != null, "Unable to access MessageSourceMap value from null key");
		return new MessageSourceMapValueImpl(messageSource, key.toString(), NO_ARGUMENTS);
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
	public static interface Value extends Map<Object, Value>, MessageSourceResolvable {
	}

	/**
	 * Internal implementation of {@link MessageSourceResolvable}.
	 */
	private class MessageSourceMapValueImpl extends AbstractMap<Object, Value> implements Value {

		private MessageSource messageSource;
		private String code;
		private String[] codes;
		private Object[] arguments;

		public MessageSourceMapValueImpl(MessageSource messageSource, String code, Object[] arguments) {
			this.messageSource = messageSource;
			this.code = code;
			this.codes = buildPrefixedCodes(code);
			this.arguments = arguments;
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
		public Value get(Object key) {
			Object[] childArguments = new Object[arguments.length + 1];
			System.arraycopy(arguments, 0, childArguments, 0, arguments.length);
			childArguments[childArguments.length - 1] = resolveMessageArgument(key);
			return new MessageSourceMapValueImpl(messageSource, code, childArguments);
		}

		public String[] getCodes() {
			return codes;
		}

		public Object[] getArguments() {
			return arguments;
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

		@Override
		public Set<Map.Entry<Object, Value>> entrySet() {
			throw new UnsupportedOperationException();
		}
	}
}

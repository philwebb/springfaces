package org.springframework.springfaces.message.ui;

import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
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

	private static final LocaleProvider NULL_LOCALE = new LocaleProvider() {
		public Locale getLocale() {
			return null;
		};
	};

	/**
	 * The message source used to resolve messages.
	 */
	private MessageSource messageSource;

	/**
	 * The locale provider
	 */
	private LocaleProvider localeProvider;

	/**
	 * The prefix codes.
	 */
	private String[] prefixCodes;

	/**
	 * Create a new MessageSourceMap
	 * @param messageSource a non-null message source
	 * @param prefixCodes prefixes that should be applied to codes being resolved or <tt>null</tt> if no prefixes are
	 * required. Prefixes should be specified in the order that they are tried
	 * @param localeProvider provides access to the {@link Locale} that should be used when resolving messages. Both
	 * this parameter and the return from the <tt>Callable</tt> can be <tt>null</tt>
	 */
	public MessageSourceMap(MessageSource messageSource, String[] prefixCodes, LocaleProvider localeProvider) {
		Assert.notNull(messageSource, "MessageSource must not be null");
		this.messageSource = messageSource;
		this.prefixCodes = (prefixCodes == null ? NO_PREFIX_CODES : prefixCodes);
		this.localeProvider = (localeProvider == null ? NULL_LOCALE : localeProvider);
	}

	protected Object resolveMessageArgument(Object argument) {
		return argument;
	}

	@Override
	public Value get(Object key) {
		Assert.state(key != null, "Unable to access MessageSourceMap value from null key");
		String[] codes = buildPrefixedCodes(key.toString());
		return new MessageSourceMapValueImpl(messageSource, codes, NO_ARGUMENTS);
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
	 * Strategy interface used to obtain the locale. Allows for the local to be obtained after the map has been created.
	 */
	public static interface LocaleProvider {

		/**
		 * Returns the Locale to use.
		 * @return the Locale or <tt>null</tt>
		 */
		public Locale getLocale();
	}

	/**
	 * Internal implementation of {@link MessageSourceResolvable}.
	 */
	private class MessageSourceMapValueImpl extends AbstractMap<Object, Value> implements Value {

		private MessageSource messageSource;
		private String[] codes;
		private Object[] arguments;

		public MessageSourceMapValueImpl(MessageSource messageSource, String[] codes, Object[] arguments) {
			this.messageSource = messageSource;
			this.codes = codes;
			this.arguments = arguments;
		}

		@Override
		public Value get(Object key) {
			Object[] childArguments = new Object[arguments.length + 1];
			System.arraycopy(arguments, 0, childArguments, 0, arguments.length);
			childArguments[childArguments.length - 1] = resolveMessageArgument(key);
			return new MessageSourceMapValueImpl(messageSource, codes, childArguments);
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
			return messageSource.getMessage(this, localeProvider.getLocale());
		}

		@Override
		public Set<Map.Entry<Object, Value>> entrySet() {
			throw new UnsupportedOperationException();
		}
	}
}

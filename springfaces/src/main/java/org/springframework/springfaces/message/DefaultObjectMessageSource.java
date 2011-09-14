package org.springframework.springfaces.message;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.springfaces.message.code.resolver.EnumMessageCodeResolver;
import org.springframework.springfaces.message.code.resolver.MessageCodeResolver;
import org.springframework.springfaces.message.code.resolver.ObjectMessageCodeResolver;
import org.springframework.util.Assert;

/**
 * Default implementation of {@link ObjectMessageSource} that is backed by a Spring {@link MessageSource}. This
 * implementation uses {@link ObjectMessageCodeResolver}s to resolve the message codes used against the
 * {@link MessageSource}. By default {@link EnumMessageCodeResolver enum} and {@link ObjectMessageCodeResolver object}
 * resolves are included.Additional resolvers can be added using the
 * {@link #addMessageCodeResolver(MessageCodeResolver)} method.
 * <p>
 * Parameterized messages are supported by this resolver.
 * 
 * @author Phillip Webb
 */
public class DefaultObjectMessageSource extends AbstractObjectMessageSource {

	/**
	 * ThreadLocal used to expose the {@link Context} to the {@link MessageCodeResolverAdapter}.
	 */
	private static ThreadLocal<Context> contextHolder = new ThreadLocal<Context>();

	/**
	 * The message source used to obtain the messages.
	 */
	private MessageSource messageSource;

	/**
	 * A conversion service that is used to manage the {@link MessageCodeResolver}s. Resolvers are wrapped in
	 * {@link MessageCodeResolverAdapter}s when they are added.
	 */
	private DefaultConversionService conversionService;

	/**
	 * Create a new {@link DefaultObjectMessageSource} instance.
	 * @param messageSource the underlying message source.
	 */
	public DefaultObjectMessageSource(MessageSource messageSource) {
		Assert.notNull(messageSource, "MessageSource must not be null");
		this.messageSource = messageSource;
		recreateConversionService();
		addMessageCodeResolver(new ObjectMessageCodeResolver());
		addMessageCodeResolver(new EnumMessageCodeResolver());
	}

	private void recreateConversionService() {
		conversionService = new DefaultConversionService();
		// Remove the ObjectToObject converter otherwise it will try to resolve MessageCodes.
		conversionService.removeConvertible(Object.class, Object.class);
	}

	@Override
	public boolean containsMessage(Class<?> type) {
		Assert.notNull(type, "Type must not be null");
		contextHolder.set(new Context(messageSource, null));
		try {
			return conversionService.canConvert(type, MessageCode.class);
		} finally {
			contextHolder.set(null);
		}
	}

	protected String resolveMessage(Object object, Locale locale) {
		contextHolder.set(new Context(messageSource, locale));
		try {
			if (!conversionService.canConvert(object.getClass(), MessageCode.class)) {
				return null;
			}
			MessageCode code = conversionService.convert(object, MessageCode.class);
			return messageSource.getMessage(new DefaultMessageSourceResolvable(code.toString()), locale);
		} finally {
			contextHolder.set(null);
		}
	}

	@Override
	protected String resolveToString(Object object) {
		if (conversionService.canConvert(object.getClass(), String.class)) {
			return conversionService.convert(object, String.class);
		}
		return super.resolveToString(object);
	}

	/**
	 * Add a message source resolver this source.
	 * @param messageCodeResolver the message source resolver
	 * @see #setMessageCodeResolvers(Collection)
	 */
	public void addMessageCodeResolver(MessageCodeResolver<?> messageCodeResolver) {
		Assert.notNull(messageCodeResolver, "MessageCodeResolver must not be null");
		conversionService.addConverter(MessageCodeResolverAdapter.newInstance(messageCodeResolver));
		reset();
	}

	/**
	 * Set the message code resolvers that should be used with this source. This method will replace any existing
	 * resolvers (including the default resolvers).
	 * @param messageCodeResolvers the resolvers to used
	 */
	public void setMessageCodeResolvers(Collection<? extends MessageCodeResolver<?>> messageCodeResolvers) {
		Assert.notNull(messageCodeResolvers, "MessageCodeResolvers must not be null");
		recreateConversionService();
		reset();
		for (MessageCodeResolver<?> messageCodeResolver : messageCodeResolvers) {
			addMessageCodeResolver(messageCodeResolver);
		}
	}

	/**
	 * An internal context used to expose details to the {@link MessageCodeResolverAdapter}. This allows us to reuse the
	 * logic from the {@link DefaultConversionService}.
	 */
	private static class Context {

		private MessageSource messageSource;
		private Locale locale;

		public Context(MessageSource messageSource, Locale locale) {
			this.messageSource = messageSource;
			this.locale = locale;
		}

		public MessageSource getMessageSource() {
			return messageSource;
		}

		public Locale getLocale() {
			return locale;
		}
	}

	/**
	 * Represents a message code. Used rather than Strings to ensure only relevant {@link MessageCodeResolverAdapter}s
	 * are used.
	 */
	private static class MessageCode {
		private String code;

		public MessageCode(String code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return code;
		}
	}

	/**
	 * Adapter class used to convert a {@link MessageCodeResolver} into a {@link ConditionalGenericConverter}. The
	 * adapter uses the contextHolder thread local to pass relevant details though to the {@link MessageCodeResolver}.
	 * @param <T> the source type
	 */
	private static class MessageCodeResolverAdapter<T> implements ConditionalGenericConverter {

		private static final Object[] NO_ARGUMENTS = {};

		private MessageCodeResolver<T> messageCodeResolver;

		private Set<ConvertiblePair> convertibleTypes;

		public MessageCodeResolverAdapter(MessageCodeResolver<T> messageConverter) {
			this.messageCodeResolver = messageConverter;
			Class<?> sourceType = GenericTypeResolver.resolveTypeArgument(messageConverter.getClass(),
					MessageCodeResolver.class);
			this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, MessageCode.class));
		}

		public static <T> MessageCodeResolverAdapter<T> newInstance(MessageCodeResolver<T> messageConverter) {
			return new MessageCodeResolverAdapter<T>(messageConverter);
		}

		public Set<ConvertiblePair> getConvertibleTypes() {
			return convertibleTypes;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
			Context context = contextHolder.get();
			if (context == null) {
				return false;
			}
			List<String> codes = messageCodeResolver.getMessageCodesForType((Class) sourceType.getType());
			return findMessageCode(codes) != null;
		}

		@SuppressWarnings("unchecked")
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			List<String> codes = messageCodeResolver.getMessageCodesForObject((T) source);
			return findMessageCode(codes);
		}

		/**
		 * Returns the first code from the list that is contained within the {@link MessageSource}.
		 * @param codes a list of codes in the order that they should be tried
		 * @return a {@link MessageCode} or <tt>null</tt> if not match is found.
		 */
		private MessageCode findMessageCode(List<String> codes) {
			Context context = contextHolder.get();
			if (codes != null) {
				MessageSource messageSource = context.getMessageSource();
				Locale locale = context.getLocale();
				for (String code : codes) {
					if (containsCode(messageSource, code, locale)) {
						return new MessageCode(code);
					}
				}
			}
			return null;
		}

		/**
		 * Determines of the given {@link MessageSource} contains the specified code.
		 * @param messageSource the messages source
		 * @param code the code to find
		 * @param locale the locale (or <tt>null</tt> to used the default locale)
		 * @return <tt>true</tt> if the {@link MessageSource} contains the code.
		 */
		public static boolean containsCode(MessageSource messageSource, String code, Locale locale) {
			if (locale == null) {
				locale = Locale.getDefault();
			}
			try {
				String message = messageSource.getMessage(code, NO_ARGUMENTS, locale);
				// if useCodeAsDefaultMessage is set then an exception will not be thrown
				if (message.equals(code)) {
					return false;
				}
			} catch (NoSuchMessageException e) {
				return false;
			}
			return true;
		}
	}
}

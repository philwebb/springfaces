package org.springframework.springfaces.message;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;

public class DefaultMessageResolver implements MessageResolver {

	private static ThreadLocal<Context> contextLocal = new ThreadLocal<Context>();

	private MessageSource messageSource;

	private DefaultConversionService conversionService;

	public DefaultMessageResolver(MessageSource messageSource) {
		this.messageSource = messageSource;
		conversionService = new DefaultConversionService();
		addConverter(new ObjectMessageCodeConverter());
		addConverter(new EnumMessageCodeConverter());
	}

	public boolean canResolveMessage(Class<?> type) {
		Assert.notNull(type, "Type must not be null");
		contextLocal.set(new Context(messageSource));
		try {
			return conversionService.canConvert(type, MessageCode.class);
		} finally {
			contextLocal.set(null);
		}
	}

	public String resolveMessage(Object object, Locale locale) {
		if (object == null) {
			return null;
		}
		contextLocal.set(new Context(messageSource));
		try {
			return doResolveMessage(object, locale, true);
		} finally {
			contextLocal.set(null);
		}
	}

	private String doResolveMessage(Object object, Locale locale, boolean onlyFromCode) {
		MessageCode code = conversionService.convert(object, MessageCode.class);
		String message = messageSource.getMessage(new DefaultMessageSourceResolvable(code.toString()), locale);
		return message;
	}

	public void addConverter(MessageCodeConverter<?> converter) {
		conversionService.addConverter(MessageCodeConverterAdapter.newInstance(converter));
	}

	private static class Context {

		private MessageSource messageSource;

		public Context(MessageSource messageSource) {
			this.messageSource = messageSource;
		}

		public MessageSource getMessageSource() {
			return messageSource;
		}
	}

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

	private static class MessageCodeConverterAdapter<T> implements ConditionalGenericConverter {

		private MessageCodeConverter<T> messageConverter;
		private Set<ConvertiblePair> convertibleTypes;

		public MessageCodeConverterAdapter(MessageCodeConverter<T> messageConverter) {
			this.messageConverter = messageConverter;
			Class<?> sourceType = GenericTypeResolver.resolveTypeArgument(messageConverter.getClass(),
					MessageCodeConverter.class);
			this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, MessageCode.class));
		}

		public static <T> MessageCodeConverterAdapter<T> newInstance(MessageCodeConverter<T> messageConverter) {
			return new MessageCodeConverterAdapter<T>(messageConverter);
		}

		public Set<ConvertiblePair> getConvertibleTypes() {
			return convertibleTypes;
		}

		public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
			Context context = contextLocal.get();
			if (context == null) {
				return false;
			}
			MessageSource messageSource = context.getMessageSource();
			return messageConverter.canConvert(messageSource, sourceType.getType());
		}

		@SuppressWarnings("unchecked")
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			Context context = contextLocal.get();
			MessageSource messageSource = context.getMessageSource();
			return new MessageCode(messageConverter.convert(messageSource, (T) source));
		}
	}

}

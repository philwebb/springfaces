package org.springframework.springfaces.message;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;

public class DefaultMessageResolver implements MessageResolver {

	private static ThreadLocal<Context> contextLocal = new ThreadLocal<Context>();

	private MessageSource messageSource;

	private ConversionService conversionService;

	public DefaultMessageResolver(MessageSource messageSource) {
		this.messageSource = messageSource;
		conversionService = new DefaultConversionService();
		((DefaultConversionService) conversionService).addConverter(MessageConverterAdapter
				.newInstance(new ObjectMessageCodeConverter()));
		((DefaultConversionService) conversionService).addConverter(MessageConverterAdapter
				.newInstance(new EnumMessageCodeConverter()));
	}

	public boolean canResolveMessage(Class<?> type) {
		// TODO Auto-generated method stub
		return false;
	}

	public String resolveMessage(Object object, Locale locale) {
		if (object == null) {
			return null;
		}
		contextLocal.set(new Context(messageSource));
		try {
			MessageCode code = conversionService.convert(object, MessageCode.class);
			return messageSource.getMessage(new DefaultMessageSourceResolvable(code.toString()), locale);
		} finally {
			contextLocal.set(null);
		}
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

	private static class MessageConverterAdapter<T> implements ConditionalGenericConverter {

		private MessageCodeConverter<T> messageConverter;
		private Set<ConvertiblePair> convertibleTypes;

		public MessageConverterAdapter(MessageCodeConverter<T> messageConverter) {
			this.messageConverter = messageConverter;
			Class<?> sourceType = GenericTypeResolver.resolveTypeArgument(messageConverter.getClass(),
					MessageCodeConverter.class);
			this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, MessageCode.class));
		}

		public static <T> MessageConverterAdapter<T> newInstance(MessageCodeConverter<T> messageConverter) {
			return new MessageConverterAdapter<T>(messageConverter);
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

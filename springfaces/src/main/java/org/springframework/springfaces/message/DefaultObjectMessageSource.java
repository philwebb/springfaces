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
import org.springframework.springfaces.message.resolver.EnumMessageCodeResolver;
import org.springframework.springfaces.message.resolver.MessageCodeResolver;
import org.springframework.springfaces.message.resolver.ObjectMessageCodeResolver;
import org.springframework.util.Assert;

public class DefaultObjectMessageSource extends AbstractObjectMessageSource {

	private static ThreadLocal<Context> contextHolder = new ThreadLocal<Context>();

	private MessageSource messageSource;

	private DefaultConversionService conversionService;

	public DefaultObjectMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
		conversionService = new DefaultConversionService();
		conversionService.removeConvertible(Object.class, Object.class);
		addMessageCodeResolver(new ObjectMessageCodeResolver());
		addMessageCodeResolver(new EnumMessageCodeResolver());
	}

	@Override
	public boolean isSupported(Class<?> type) {
		Assert.notNull(type, "Type must not be null");
		contextHolder.set(new Context(messageSource));
		try {
			return conversionService.canConvert(type, MessageCode.class);
		} finally {
			contextHolder.set(null);
		}
	}

	protected String resolveMessage(Object object, Locale locale) {
		contextHolder.set(new Context(messageSource));
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
	protected String toString(Object object) {
		if (!conversionService.canConvert(object.getClass(), String.class)) {
			return conversionService.convert(object, String.class);
		}
		return super.toString(object);
	}

	public void addMessageCodeResolver(MessageCodeResolver<?> resolver) {
		conversionService.addConverter(MessageCodeResolverAdapter.newInstance(resolver));
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

	private static class MessageCodeResolverAdapter<T> implements ConditionalGenericConverter {

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

		public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
			Context context = contextHolder.get();
			if (context == null) {
				return false;
			}
			MessageSource messageSource = context.getMessageSource();
			return messageCodeResolver.canResolve(messageSource, sourceType.getType());
		}

		@SuppressWarnings("unchecked")
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			Context context = contextHolder.get();
			MessageSource messageSource = context.getMessageSource();
			return new MessageCode(messageCodeResolver.resolve(messageSource, (T) source));
		}
	}
}

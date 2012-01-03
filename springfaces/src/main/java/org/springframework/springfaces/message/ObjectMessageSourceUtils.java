package org.springframework.springfaces.message;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.Assert;

/**
 * Miscellaneous {@link ObjectMessageSource} utility methods.
 * 
 * @author Phillip Webb
 */
public abstract class ObjectMessageSourceUtils {

	/**
	 * Get an {@link ObjectMessageSource} from the specified <tt>messageSource</tt> falling back to a Spring
	 * {@link ApplicationContext} if <tt>messageSource</tt> is <tt>null</tt>. If the resulting <tt>messageSource</tt>
	 * cannot be cast to an {@link ObjectMessageSource} a new {@link DefaultObjectMessageSource} will be returned.
	 * @param messageSource the message source (if <tt>null</tt> if the <tt>fallbackApplicationContext</tt> will be
	 * used)
	 * @param fallbackApplicationContext a fallback {@link ApplicationContext} to be used if the <tt>messageSource</tt>
	 * parameter is <tt>null</tt>. This parameter may be <tt>null</tt> as long as the <tt>messageSource</tt> paramter is
	 * not <tt>null</tt>
	 * @return a {@link ObjectMessageSource} instance
	 */
	public static ObjectMessageSource getObjectMessageSource(MessageSource messageSource,
			ApplicationContext fallbackApplicationContext) {
		if (messageSource == null) {
			messageSource = getMessageSource(fallbackApplicationContext);
		}
		if (messageSource instanceof ObjectMessageSource) {
			return (ObjectMessageSource) messageSource;
		}
		return new DefaultObjectMessageSource(messageSource);
	}

	/**
	 * Get a {@link MessageSource} for the given {@link ApplicationContext}. This method will attempt to access the
	 * message source bean directly so that it can be cast to an {@link ObjectMessageSource} instance. If the message
	 * source bean cannot be accessed the {@link ApplicationContext} itself is returned.
	 * @param applicationContext the application context
	 * @return a message source instance
	 */
	private static MessageSource getMessageSource(ApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "ApplicationContext must not be null");
		if (applicationContext.containsBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)) {
			Object messageSourceBean = applicationContext.getBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME);
			if (messageSourceBean instanceof MessageSource) {
				return (MessageSource) messageSourceBean;
			}
		}
		return applicationContext;
	}
}

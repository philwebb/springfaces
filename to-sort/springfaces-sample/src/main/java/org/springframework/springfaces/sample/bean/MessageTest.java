package org.springframework.springfaces.sample.bean;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.springfaces.message.ui.MessageSourceMap;
import org.springframework.stereotype.Component;

@Component
public class MessageTest implements MessageSourceAware {

	private MessageSource messageSource;

	public MessageSourceMap getMessages() {
		return new MessageSourceMap(messageSource, null, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}

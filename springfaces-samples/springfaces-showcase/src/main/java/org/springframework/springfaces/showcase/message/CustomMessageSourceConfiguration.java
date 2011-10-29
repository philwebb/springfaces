package org.springframework.springfaces.showcase.message;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticMessageSource;

@Configuration
public class CustomMessageSourceConfiguration {
	@Bean
	public MessageSource exampleMessageSource() {
		StaticMessageSource source = new StaticMessageSource();
		source.addMessage("pages.message.definedsource.hello", Locale.getDefault(), "Hello Defined Source");
		return source;
	}
}

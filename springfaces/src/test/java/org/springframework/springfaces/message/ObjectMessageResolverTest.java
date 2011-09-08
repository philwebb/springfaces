package org.springframework.springfaces.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

public class ObjectMessageResolverTest {

	private static final Locale LOCALE = Locale.getDefault();

	private static final String INNER = ObjectMessageResolverTest.class.getName() + "$";

	private StaticMessageSource messageSource;

	private ObjectMessageResolver<Object> resolver = new ObjectMessageResolver<Object>();

	@Before
	public void setup() {
		this.messageSource = new StaticMessageSource();
		setupMessage(INNER + "Mapped", "mapped");
		setupMessage(INNER + "MappedArguments", "a {name} b");
	}

	private void setupMessage(String code, String msg) {
		messageSource.addMessage(code, LOCALE, msg);
	}

	@Test
	public void shouldBeForMappedClass() throws Exception {
		assertThat(resolver.isResolvable(messageSource, Mapped.class), is(true));
		assertThat(resolver.isResolvable(messageSource, NotMapped.class), is(false));
	}

	@Test
	public void shouldBeForMappedClassWhenUsingDefaults() throws Exception {
		messageSource.setUseCodeAsDefaultMessage(true);
		assertThat(resolver.isResolvable(messageSource, Mapped.class), is(true));
		assertThat(resolver.isResolvable(messageSource, NotMapped.class), is(false));
	}

	@Test
	public void shouldGetMappedAsString() throws Exception {
		String actual = resolver.resolve(messageSource, LOCALE, new Mapped());
		assertThat(actual, is("mapped"));
	}

	@Test
	public void shouldConvertNullToNull() throws Exception {
		String actual = resolver.resolve(messageSource, LOCALE, null);
		assertThat(actual, is(nullValue()));
	}

	@Test
	@Ignore
	public void shouldGetWithExpandedArguments() throws Exception {
		// FIXME
		String actual = resolver.resolve(messageSource, LOCALE, new MappedArguments("x"));
		assertThat(actual, is("a x b"));
	}

	static class NotMapped {
	}

	static class Mapped {
	}

	static class MappedArguments {
		private String name;

		public MappedArguments(String name) {
		}

		public String getName() {
			return name;
		}
	}

}

package org.springframework.springfaces.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

public class DefaultMessageResolverTest {

	// FIXME
	private static final Locale LOCALE = Locale.getDefault();

	private static final String INNER = DefaultMessageResolverTest.class.getName() + "$";

	private StaticMessageSource messageSource;

	private DefaultMessageResolver resolver;

	@Before
	public void setup() {
		this.messageSource = new StaticMessageSource();
		this.resolver = new DefaultMessageResolver(messageSource);
		setupMessage(INNER + "Mapped", "mapped");
		setupMessage(INNER + "MappedArguments", "a {name} b");
		setupMessage(INNER + "Enum.ONE", "1");
	}

	private void setupMessage(String code, String msg) {
		messageSource.addMessage(code, LOCALE, msg);
	}

	@Test
	public void shouldSupportMappedClasses() throws Exception {
		assertThat(resolver.canResolveMessage(Mapped.class), is(true));
		assertThat(resolver.canResolveMessage(NotMapped.class), is(false));
	}

	@Test
	public void shouldSupportMappedClassesWhenUsingCodeAsDefaultMessage() throws Exception {
		messageSource.setUseCodeAsDefaultMessage(true);
		assertThat(resolver.canResolveMessage(Mapped.class), is(true));
		assertThat(resolver.canResolveMessage(NotMapped.class), is(false));
	}

	@Test
	public void shouldGetMappedAsString() throws Exception {
		String actual = resolver.resolveMessage(new Mapped(), LOCALE);
		assertThat(actual, is("mapped"));
	}

	@Test
	public void shouldConvertNullToNull() throws Exception {
		String actual = resolver.resolveMessage(null, LOCALE);
		assertThat(actual, is(nullValue()));
	}

	@Test
	@Ignore
	public void shouldGetWithExpandedArguments() throws Exception {
		// FIXME
		String actual = resolver.resolveMessage(new MappedArguments("x"), LOCALE);
		assertThat(actual, is("a x b"));
	}

	@Test
	public void shouldGetEnum() throws Exception {
		String actual = resolver.resolveMessage(Enum.ONE, LOCALE);
		assertThat(actual, is("1"));
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

	enum Enum {
		ONE, TWO, THREE
	}

}

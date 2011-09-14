package org.springframework.springfaces.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.springfaces.message.code.resolver.MessageCodeResolver;

/**
 * Tests for {@link DefaultObjectMessageSource}.
 * 
 * @author Phillip Webb
 */
public class DefaultObjectMessageSourceTest {

	private static final Locale LOCALE = Locale.getDefault();

	private static final String INNER = DefaultObjectMessageSourceTest.class.getName() + "$";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private StaticMessageSource messageSource;

	private DefaultObjectMessageSource objectMessageSource;

	@Before
	public void setup() {
		this.messageSource = new StaticMessageSource();
		this.objectMessageSource = new DefaultObjectMessageSource(messageSource);
		setupMessage(INNER + "Mapped", "mapped");
		setupMessage(INNER + "MappedArguments", "a {name} b {numberEnum}");
		setupMessage(INNER + "NumberEnum.ONE", "1");
		setupMessage("custom", "customresolved");
		setupMessage(INNER + "MappedStrings", "numbers {numbers}");
		setupMessage(INNER + "MappedWithMissingParameters", "mapped {missing}");

	}

	private void setupMessage(String code, String msg) {
		messageSource.addMessage(code, LOCALE, msg);
	}

	@Test
	public void shouldContainMappedClasses() throws Exception {
		assertThat(objectMessageSource.containsMessage(Mapped.class), is(true));
		assertThat(objectMessageSource.containsMessage(NotMapped.class), is(false));
	}

	@Test
	public void shouldContainMappedClassesWhenUsingCodeAsDefaultMessage() throws Exception {
		messageSource.setUseCodeAsDefaultMessage(true);
		assertThat(objectMessageSource.containsMessage(Mapped.class), is(true));
		assertThat(objectMessageSource.containsMessage(NotMapped.class), is(false));
	}

	@Test
	public void shouldGetMappedMessage() throws Exception {
		String actual = objectMessageSource.getMessage(new Mapped(), LOCALE);
		assertThat(actual, is("mapped"));
	}

	@Test
	public void shouldGetNullMessageForNullObject() throws Exception {
		String actual = objectMessageSource.getMessage(null, LOCALE);
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void shouldGetMessageForEnum() throws Exception {
		String actual = objectMessageSource.getMessage(NumberEnum.ONE, LOCALE);
		assertThat(actual, is("1"));
	}

	@Test
	public void shouldGetMessageWithExpandedArguments() throws Exception {
		String actual = objectMessageSource.getMessage(new MappedArguments("x", NumberEnum.ONE), LOCALE);
		assertThat(actual, is("a x b 1"));
	}

	@Test
	public void shouldGetMessageWithExpandedNullArgument() throws Exception {
		String actual = objectMessageSource.getMessage(new MappedArguments(null, NumberEnum.ONE), LOCALE);
		assertThat(actual, is("a  b 1"));
	}

	@Test
	public void shouldAddCustomResolver() throws Exception {
		objectMessageSource.addMessageCodeResolver(new CustomMessageCodeResolver());
		String actual = objectMessageSource.getMessage(new Mapped(), LOCALE);
		assertThat(actual, is("customresolved"));
	}

	@Test
	public void shouldReplaceDefaultResolversWhenSettingResolver() throws Exception {
		objectMessageSource.getMessage(NumberEnum.ONE, LOCALE);
		objectMessageSource.setMessageCodeResolvers(Collections.singleton(new CustomMessageCodeResolver()));
		String actual = objectMessageSource.getMessage(new Mapped(), LOCALE);
		assertThat(actual, is("customresolved"));
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The object type " + INNER + "NumberEnum is not supported");
		objectMessageSource.getMessage(NumberEnum.ONE, LOCALE);
	}

	@Test
	public void shouldUseConverterToGenerateStrings() throws Exception {
		String actual = objectMessageSource.getMessage(new MappedStrings(1, 2, 3, 4), LOCALE);
		assertThat(actual, is("numbers 1,2,3,4"));
	}

	@Test
	public void shouldLeaveUnmachedParameters() throws Exception {
		String actual = objectMessageSource.getMessage(new MappedWithMissingParameters(), LOCALE);
		assertThat(actual, is("mapped {missing}"));
	}

	@Test
	public void shouldCacheIfNotParameters() throws Exception {
		Field field = AbstractObjectMessageSource.class.getDeclaredField("containsParameter");
		field.setAccessible(true);
		Map<?, ?> cache = (Map<?, ?>) field.get(objectMessageSource);
		assertThat(cache.isEmpty(), is(true));
		objectMessageSource.getMessage(new Mapped(), LOCALE);
		assertThat(cache.isEmpty(), is(false));
		objectMessageSource.getMessage(new Mapped(), LOCALE);
	}

	static class NotMapped {
	}

	static class Mapped {
	}

	static class MappedArguments {
		private String name;
		private NumberEnum numberEnum;

		public MappedArguments(String name, NumberEnum numberEnum) {
			this.name = name;
			this.numberEnum = numberEnum;
		}

		public String getName() {
			return name;
		}

		public NumberEnum getNumberEnum() {
			return numberEnum;
		}
	}

	static class MappedStrings {
		private int[] numbers;

		public MappedStrings(int... numbers) {
			this.numbers = numbers;
		}

		public int[] getNumbers() {
			return numbers;
		}
	}

	static class MappedWithMissingParameters {
	}

	enum NumberEnum {
		ONE, TWO, THREE
	}

	static class CustomMessageCodeResolver implements MessageCodeResolver<Mapped> {

		public List<String> getMessageCodesForObject(Mapped object) {
			return Collections.singletonList("custom");
		}

		public List<String> getMessageCodesForType(Class<? extends Mapped> type) {
			return Collections.singletonList("custom");
		}
	}
}

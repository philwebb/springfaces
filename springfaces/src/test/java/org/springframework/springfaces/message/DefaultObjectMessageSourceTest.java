package org.springframework.springfaces.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.support.StaticMessageSource;

/**
 * Tests for {@link DefaultObjectMessageSource}.
 * 
 * @author Phillip Webb
 */
public class DefaultObjectMessageSourceTest {

	private static final Locale LOCALE = Locale.UK;

	private static final String INNER = DefaultObjectMessageSourceTest.class.getName() + "$";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private StaticMessageSource parent;

	private DefaultObjectMessageSource messageSource;

	@Before
	public void setup() {
		this.parent = new StaticMessageSource();
		this.messageSource = new DefaultObjectMessageSource();
		this.messageSource.setParentMessageSource(parent);
		addMessageToParent(INNER + "Mapped", "mapped");
		addMessageToParent(INNER + "MappedArguments", "a {name} b {numberEnum}");
		addMessageToParent(INNER + "NumberEnum.ONE", "1");
		addMessageToParent(INNER + "MappedArray", "numbers {numbers}");
		addMessageToParent(INNER + "MappedCollection", "collection {collection}");
		addMessageToParent(INNER + "MappedWithMissingParameters", "mapped {missing}");
		addMessageToParent(INNER + "MappedWithArguments", "mapped args {1} {0}");
	}

	private void addMessageToParent(String code, String msg) {
		parent.addMessage(code, LOCALE, msg);
	}

	@Test
	public void shouldGetMappedMessage() throws Exception {
		String actual = messageSource.getMessage(new Mapped(), null, LOCALE);
		assertThat(actual, is("mapped"));
	}

	@Test
	public void shouldGetNullMessageForNullObject() throws Exception {
		String actual = messageSource.getMessage((Object) null, null, LOCALE);
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void shouldGetMessageForEnum() throws Exception {
		String actual = messageSource.getMessage(NumberEnum.ONE, null, LOCALE);
		assertThat(actual, is("1"));
	}

	@Test
	public void shouldGetMessageWithExpandedArguments() throws Exception {
		String actual = messageSource.getMessage(new MappedArguments("x", NumberEnum.ONE), null, LOCALE);
		assertThat(actual, is("a x b 1"));
	}

	@Test
	public void shouldGetMessageWithExpandedNullArgument() throws Exception {
		String actual = messageSource.getMessage(new MappedArguments(null, NumberEnum.ONE), null, LOCALE);
		assertThat(actual, is("a  b 1"));
	}

	@Test
	public void shouldGetMessageWithExpandedArrayArguments() throws Exception {
		String actual = messageSource.getMessage(new MappedArray(1, 2, 3, 4), null, LOCALE);
		assertThat(actual, is("numbers 1,2,3,4"));
	}

	@Test
	public void shouldGetMessageWithExpandedCollectionArguments() throws Exception {
		String actual = messageSource.getMessage(new MappedCollection(NumberEnum.ONE, 2, 3, 4), null, LOCALE);
		assertThat(actual, is("collection 1,2,3,4"));
	}

	@Test
	public void shouldLeaveUnmachedParameters() throws Exception {
		String actual = messageSource.getMessage(new MappedWithMissingParameters(), null, LOCALE);
		assertThat(actual, is("mapped {missing}"));
	}

	@Test
	public void shouldThrowNoSuchObjectExceptionIfNotMapped() throws Exception {
		thrown.expect(NoSuchObjectMessageException.class);
		thrown.expectMessage("Unable to convert object of type " + INNER + "NotMapped to a message for locale en_GB");
		messageSource.getMessage(new NotMapped(), null, LOCALE);
	}

	@Test
	public void shouldResolveMessageWithArguments() throws Exception {
		String actual = messageSource.getMessage(new MappedWithArguments(), new Object[] { 1, 2 }, LOCALE);
		assertThat(actual, is("mapped args 2 1"));
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

	static class MappedArray {
		private int[] numbers;

		public MappedArray(int... numbers) {
			this.numbers = numbers;
		}

		public int[] getNumbers() {
			return numbers;
		}
	}

	static class MappedCollection {
		private List<Object> collection;

		public MappedCollection(Object... objects) {
			this.collection = Arrays.asList(objects);
		}

		public List<Object> getCollection() {
			return collection;
		}
	}

	static class MappedWithMissingParameters {
	}

	static class MappedWithArguments {
	}

	enum NumberEnum {
		ONE, TWO, THREE
	}
}

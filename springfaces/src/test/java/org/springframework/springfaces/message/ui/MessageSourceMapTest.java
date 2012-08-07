/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.message.ui;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.springfaces.message.DefaultObjectMessageSource;
import org.springframework.springfaces.message.NoSuchObjectMessageException;
import org.springframework.springfaces.message.ObjectMessageSource;

/**
 * Tests for {@link MessageSourceMap}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageSourceMapTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldNeedMessageSource() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("MessageSource must not be null");
		new TestMessageSourceMap(null, null);
	}

	@Test
	public void shouldThrowUnsupportedOperationExceptions() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource(), null);
		this.thrown.expect(UnsupportedOperationException.class);
		map.size();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldThrowNestedUnsupportedOperationExceptions() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource(), null);
		Object value = map.get("x", "y");
		this.thrown.expect(UnsupportedOperationException.class);
		((Map<Object, Object>) value).size();
	}

	@Test
	public void shouldAllowNullKeys() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource(), null);
		Object actual = map.get((Object) null);
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void shouldGetValue() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource(), null);
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x");
		assertThat(value.getCodes()[0], is(equalTo("x")));
	}

	@Test
	public void shouldUsePrefixCodes() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource(), new String[] { "a.", "b.", "c." });
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x");
		assertThat(Arrays.asList(value.getCodes()), is(equalTo(Arrays.asList("a.x", "b.x", "c.x"))));
	}

	@Test
	public void shouldResolveOnGetValueToString() throws Exception {
		StaticMessageSource messageSource = new StaticMessageSource();
		MessageSourceMap map = new TestMessageSourceMap(messageSource, null);
		Object value = map.get("x");
		messageSource.addMessage("x", Locale.US, "message");
		assertThat(value.toString(), is(equalTo("message")));
	}

	@Test
	public void shouldUsePrefixCodesOnGetValueToString() throws Exception {
		MessageSource messageSource = mock(MessageSource.class);
		MessageSourceMap map = new TestMessageSourceMap(messageSource, new String[] { "a.", "b.", "c." });
		Object value = map.get("x");
		given(messageSource.getMessage(msr("a.x", "b.x", "c.x"), nullLocale())).willReturn("message");
		assertThat(value.toString(), is(equalTo("message")));
	}

	@Test
	public void shouldAllowNesting() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource());
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x", "y", "z");
		assertThat(value.getCodes()[0], is(equalTo("x")));
		assertThat(Arrays.asList(value.getArguments()), is(equalTo(Arrays.<Object> asList("y", "z"))));
	}

	@Test
	public void shouldUsePrefixCodesWhenNesting() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource(), new String[] { "a.", "b.", "c." });
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x", "y", "z");
		assertThat(Arrays.asList(value.getCodes()), is(equalTo(Arrays.asList("a.x", "b.x", "c.x"))));
		assertThat(Arrays.asList(value.getArguments()), is(equalTo(Arrays.<Object> asList("y", "z"))));
	}

	@Test
	public void shouldSupportNullPrefixCodesWhenNesting() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource(), new String[] { "a.", null, "c." });
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x", "y", "z");
		assertThat(Arrays.asList(value.getCodes()), is(equalTo(Arrays.asList("a.x", "x", "c.x"))));
		assertThat(Arrays.asList(value.getArguments()), is(equalTo(Arrays.<Object> asList("y", "z"))));
	}

	@Test
	public void shouldHaveToString() throws Exception {
		MessageSource messageSource = mock(MessageSource.class, "messageSource");
		MessageSourceMap map = new TestMessageSourceMap(messageSource, new String[] { "a.", "b.", "c." });
		assertThat(map.toString(),
				containsString("messageSource = messageSource, prefixCodes = array<String>['a.', 'b.', 'c.']]"));
	}

	@Test
	public void shouldUseLocale() throws Exception {
		final Locale locale = Locale.ITALY;
		StaticMessageSource messageSource = new StaticMessageSource();
		MessageSourceMap map = new TestMessageSourceMap(messageSource) {
			@Override
			protected Locale getLocale() {
				return locale;
			};
		};
		Object value = map.get("x");
		messageSource.addMessage("x", locale, "message");
		assertThat(value.toString(), is(equalTo("message")));
	}

	@Test
	public void shouldHaveNullDefaultMessage() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource());
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x");
		assertThat(value.getDefaultMessage(), is(nullValue()));
	}

	@Test
	public void shouldSupportTopLevelObjectWhenBackedWithObjectMessageSource() throws Exception {
		StaticMessageSource messageSource = new StaticMessageSource();
		MessageSourceMap map = new TestMessageSourceMap(new DefaultObjectMessageSource(messageSource));
		ObjectResolvable resolvable = new ObjectResolvable();
		messageSource.addMessage(ObjectResolvable.class.getName(), Locale.US, "test");
		String actual = map.get(resolvable).toString();
		assertThat(actual, is(equalTo("test")));
	}

	@Test
	public void shouldSupportTopLevelObjectWithArgumentsWhenBackedWithObjectMessageSource() throws Exception {
		ObjectMessageSource objectMessageSource = mock(ObjectMessageSource.class);
		MessageSourceMap map = new TestMessageSourceMap(objectMessageSource);
		ObjectResolvable resolvable = new ObjectResolvable();
		String expected = "test";
		given(objectMessageSource.getMessage((Object) eq("y"), emptyObjectArray(), nullLocale())).willReturn("y2");
		given(objectMessageSource.getMessage((Object) eq("z"), emptyObjectArray(), nullLocale())).willThrow(
				new NoSuchObjectMessageException("z", null));
		given(objectMessageSource.getMessage(eq(resolvable), eq(new Object[] { "y2", "z" }), nullLocale())).willReturn(
				expected);
		String actual = map.get(resolvable, "y", "z").toString();
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void shouldResolveParamtersUsingObjectMessageSource() throws Exception {
		StaticMessageSource messageSource = new StaticMessageSource();
		MessageSourceMap map = new TestMessageSourceMap(new DefaultObjectMessageSource(messageSource));
		ObjectResolvable resolvable = new ObjectResolvable();
		messageSource.addMessage(ObjectResolvable.class.getName(), Locale.US, "test");
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x", resolvable);
		assertThat((String) value.getArguments()[0], is(equalTo("test")));
	}

	@Test
	public void shouldNotResolveTopLevelObjectIfNotBackedWithObjectMessageSource() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new StaticMessageSource());
		ObjectResolvable resolvable = new ObjectResolvable();
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Unable to resolve " + ObjectResolvable.class.getName()
				+ " messages when not using an ObjectMessageSource.");
		map.get(resolvable);
	}

	@Test
	public void shouldReturnObjectStringOnNoSuchObjectMessageException() throws Exception {
		MessageSourceMap map = new TestMessageSourceMap(new DefaultObjectMessageSource(new StaticMessageSource()));
		ObjectResolvable resolvable = new ObjectResolvable();
		String actual = map.get(resolvable).toString();
		assertThat(actual, is("Object Resolvable"));
	}

	@Test
	public void shouldReturnStringWhenRootCannotBeExpanded() throws Exception {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("test", Locale.US, "test message");
		TestMessageSourceMap map = new TestMessageSourceMap(messageSource);
		map.setReturnStringWhenPossible(true);
		Object value = map.get("test");
		assertThat(value.toString(), is("test message"));
		assertThat(value, is(String.class));
	}

	@Test
	public void shouldReturnStringWhenNestedCannotBeExpanded() throws Exception {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("test", Locale.US, "test {0} {1} message");
		TestMessageSourceMap map = new TestMessageSourceMap(messageSource);
		map.setReturnStringWhenPossible(true);
		Object value = map.get("test", "x", "y");
		assertThat(value.toString(), is("test x y message"));
		assertThat(value, is(String.class));
	}

	private Locale nullLocale() {
		return (Locale) isNull();
	}

	private Object[] emptyObjectArray() {
		return eq(new Object[] {});
	}

	private MessageSourceResolvable msr(String... codes) {
		return argThat(new MessageSourceResolvableMatcher(codes));
	}

	private static class TestMessageSourceMap extends MessageSourceMap {

		private boolean returnStringWhenPossible;

		@Override
		protected boolean returnStringsWhenPossible() {
			return this.returnStringWhenPossible;
		}

		public TestMessageSourceMap(MessageSource messageSource, String[] prefixCodes) {
			super(messageSource, prefixCodes);
		}

		public TestMessageSourceMap(MessageSource messageSource) {
			super(messageSource);
		}

		public void setReturnStringWhenPossible(boolean returnStringWhenPossible) {
			this.returnStringWhenPossible = returnStringWhenPossible;
		}
	}

	private static class MessageSourceResolvableMatcher extends ArgumentMatcher<MessageSourceResolvable> {

		private String[] codes;

		public MessageSourceResolvableMatcher(String[] codes) {
			this.codes = codes;
		}

		@Override
		public boolean matches(Object argument) {
			if (!(argument instanceof MessageSourceResolvable)) {
				return false;
			}
			MessageSourceResolvable resolvable = (MessageSourceResolvable) argument;
			return Arrays.equals(this.codes, resolvable.getCodes());
		}
	}

	static class ObjectResolvable {
		@Override
		public String toString() {
			return "Object Resolvable";
		}
	}
}

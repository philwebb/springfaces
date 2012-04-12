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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.springfaces.message.NoSuchObjectMessageException;
import org.springframework.springfaces.message.ObjectMessageSource;
import org.springframework.springfaces.message.ui.MessageSourceMap.Value;

/**
 * Tests for {@link MessageSourceMap}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageSourceMapTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private MessageSource messageSource;

	@Mock
	private ObjectMessageSource objectMessageSource;

	@Test
	public void shouldNeedMessageSource() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("MessageSource must not be null");
		new MessageSourceMap(null, null);
	}

	@Test
	public void shouldThrowUnsupportedOperationExceptions() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, null);
		this.thrown.expect(UnsupportedOperationException.class);
		map.size();
	}

	@Test
	public void shouldThrowNestedUnsupportedOperationExceptions() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, null);
		Value value = map.get("x").get("y");
		this.thrown.expect(UnsupportedOperationException.class);
		value.size();
	}

	@Test
	public void shouldAllowNullKeys() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, null);
		Value actual = map.get(null);
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void shouldGetValue() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, null);
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x");
		assertEquals("x", value.getCodes()[0]);
	}

	@Test
	public void shouldUsePrefixCodes() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, new String[] { "a.", "b.", "c." });
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x");
		assertEquals(Arrays.asList("a.x", "b.x", "c.x"), Arrays.asList(value.getCodes()));
	}

	@Test
	public void shouldResolveOnGetValueToString() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, null);
		Value value = map.get("x");
		given(this.messageSource.getMessage(msr("x"), nullLocale())).willReturn("message");
		assertEquals("message", value.toString());
	}

	@Test
	public void shouldUsePrefixCodesOnGetValueToString() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, new String[] { "a.", "b.", "c." });
		Value value = map.get("x");
		given(this.messageSource.getMessage(msr("a.x", "b.x", "c.x"), nullLocale())).willReturn("message");
		assertEquals("message", value.toString());
	}

	@Test
	public void shouldAllowNesting() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource);
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x").get("y").get("z");
		assertEquals("x", value.getCodes()[0]);
		assertEquals(Arrays.asList("y", "z"), Arrays.asList(value.getArguments()));
	}

	@Test
	public void shouldUsePrefixCodesWhenNesting() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, new String[] { "a.", "b.", "c." });
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x").get("y").get("z");
		assertEquals(Arrays.asList("a.x", "b.x", "c.x"), Arrays.asList(value.getCodes()));
		assertEquals(Arrays.asList("y", "z"), Arrays.asList(value.getArguments()));
	}

	@Test
	public void shouldSupportNullPrefixCodesWhenNesting() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, new String[] { "a.", null, "c." });
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x").get("y").get("z");
		assertEquals(Arrays.asList("a.x", "x", "c.x"), Arrays.asList(value.getCodes()));
		assertEquals(Arrays.asList("y", "z"), Arrays.asList(value.getArguments()));
	}

	@Test
	public void shouldHaveToString() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource, new String[] { "a.", "b.", "c." });
		assertTrue(map.toString().contains(
				"messageSource = messageSource, prefixCodes = array<String>['a.', 'b.', 'c.']]"));
	}

	@Test
	public void shouldUseLocale() throws Exception {
		final Locale locale = Locale.ITALY;
		MessageSourceMap map = new MessageSourceMap(this.messageSource) {
			@Override
			protected Locale getLocale() {
				return locale;
			};
		};
		Value value = map.get("x");
		given(this.messageSource.getMessage(msr("x"), eq(locale))).willReturn("message");
		assertEquals("message", value.toString());
	}

	@Test
	public void shouldHaveNullDefaultMessage() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource);
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x");
		assertNull(value.getDefaultMessage());
	}

	@Test
	public void shouldSupportTopLevelObjectWhenBackedWithObjectMessageSource() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.objectMessageSource);
		ObjectResolvable resolvable = new ObjectResolvable();
		String expected = "test";
		given(this.objectMessageSource.getMessage(eq(resolvable), emptyObjectArray(), nullLocale())).willReturn(
				expected);
		String actual = map.get(resolvable).toString();
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void shouldSupportTopLevelObjectWithArgumentsWhenBackedWithObjectMessageSource() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.objectMessageSource);
		ObjectResolvable resolvable = new ObjectResolvable();
		String expected = "test";
		given(this.objectMessageSource.getMessage((Object) eq("y"), emptyObjectArray(), nullLocale())).willReturn("y2");
		given(this.objectMessageSource.getMessage((Object) eq("z"), emptyObjectArray(), nullLocale())).willThrow(
				new NoSuchObjectMessageException("z", null));
		given(this.objectMessageSource.getMessage(eq(resolvable), eq(new Object[] { "y2", "z" }), nullLocale()))
				.willReturn(expected);
		String actual = map.get(resolvable).get("y").get("z").toString();
		assertThat(actual, is(equalTo(expected)));
	}

	@Test
	public void shouldResolveParamtersUsingObjectMessageSource() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.objectMessageSource);
		ObjectResolvable resolvable = new ObjectResolvable();
		String expected = "test";
		given(this.objectMessageSource.getMessage(eq(resolvable), emptyObjectArray(), nullLocale())).willReturn(
				expected);
		MessageSourceResolvable value = (MessageSourceResolvable) map.get("x").get(resolvable);
		assertThat((String) value.getArguments()[0], is(equalTo(expected)));
	}

	@Test
	public void shouldNotResolveTopLevelObjectIfNotBackedWithObjectMessageSource() throws Exception {
		MessageSourceMap map = new MessageSourceMap(this.messageSource);
		ObjectResolvable resolvable = new ObjectResolvable();
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Unable to resolve " + ObjectResolvable.class.getName()
				+ " messages when not using an ObjectMessageSource.");
		map.get(resolvable);
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
	}
}

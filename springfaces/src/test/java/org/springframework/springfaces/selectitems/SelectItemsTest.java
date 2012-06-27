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
package org.springframework.springfaces.selectitems;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.faces.model.SelectItem;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link SelectItems}.
 * @author Phillip Webb
 */
public class SelectItemsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldSupportNullValue() throws Exception {
		Iterator<SelectItem> iterator = newSelectItemsIterator(null);
		assertThat(iterator.hasNext(), is(false));
		this.thrown.expect(NoSuchElementException.class);
		iterator.next();
	}

	@Test
	public void shouldSupportSingleSelectItem() throws Exception {
		SelectItem selectItem = new SelectItem();
		Iterator<SelectItem> iterator = newSelectItemsIterator(selectItem);
		assertThat(iterator.next(), is(sameInstance(selectItem)));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldSupportMap() throws Exception {
		Map<Object, Object> map = new LinkedHashMap<Object, Object>();
		map.put("ka", "va");
		map.put(null, "vb");
		map.put("c", null);
		Iterator<SelectItem> iterator = newSelectItemsIterator(map);
		SelectItem a = iterator.next();
		SelectItem b = iterator.next();
		SelectItem c = iterator.next();
		assertThat(iterator.hasNext(), is(false));

		assertThat(a.getLabel(), is("ka"));
		assertThat(a.getValue(), is((Object) "va"));

		assertThat(b.getLabel(), is("vb"));
		assertThat(b.getValue(), is((Object) "vb"));

		assertThat(c.getLabel(), is("c"));
		assertThat(c.getValue(), is((Object) ""));
	}

	@Test
	public void shouldSupportArrayOfSelectItems() throws Exception {
		SelectItem a = new SelectItem();
		SelectItem b = new SelectItem();
		Iterator<SelectItem> iterator = newSelectItemsIterator(new SelectItem[] { a, b });
		assertThat(iterator.next(), is(sameInstance(a)));
		assertThat(iterator.next(), is(sameInstance(b)));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldSupportArrayOfObjects() throws Exception {
		Iterator<SelectItem> iterator = newSelectItemsIterator(new String[] { "a", "b" });
		assertThat(iterator.next().getValue(), is((Object) "a"));
		assertThat(iterator.next().getValue(), is((Object) "b"));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldSupportIterableOfSelectItems() throws Exception {
		SelectItem a = new SelectItem();
		SelectItem b = new SelectItem();
		Iterator<SelectItem> iterator = newSelectItemsIterator(Arrays.asList(a, b));
		assertThat(iterator.next(), is(sameInstance(a)));
		assertThat(iterator.next(), is(sameInstance(b)));
		assertThat(iterator.hasNext(), is(false));

	}

	@Test
	public void shouldSupportIterableOfObjects() throws Exception {
		Iterator<SelectItem> iterator = newSelectItemsIterator(Arrays.asList("a", "b"));
		assertThat(iterator.next().getValue(), is((Object) "a"));
		assertThat(iterator.next().getValue(), is((Object) "b"));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldNotSupportOtherObjectTypes() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Unsupport class type java.lang.StringBuffer for SelectItem value");
		new TestableSelectItems(new StringBuffer()).iterator();
	}

	@Test
	public void shouldReturnFirstNonNull() throws Exception {
		new TestableSelectItems() {
			@Override
			public void test() throws Exception {
				Object v = firstNonNullValue(null, null, "a", "b");
				assertThat(v, is((Object) "a"));
			}
		}.test();
	}

	@Test
	public void shouldSupportFirstNonNullWithAllNulls() throws Exception {
		new TestableSelectItems() {
			@Override
			public void test() throws Exception {
				Object v = firstNonNullValue(null, null, null);
				assertThat(v, is(nullValue()));
			}
		}.test();

	}

	@Test
	public void shouldGetBooleanValue() throws Exception {
		new TestableSelectItems() {
			@Override
			public void test() throws Exception {
				assertThat(getBooleanValue("false"), is(false));
				assertThat(getBooleanValue("true"), is(true));
				assertThat(getBooleanValue("xxx"), is(false));
				assertThat(getBooleanValue(Boolean.FALSE), is(false));
				assertThat(getBooleanValue(Boolean.TRUE), is(true));
				assertThat(getBooleanValue(null), is(false));
			}
		}.test();

	}

	@Test
	public void shouldGetBooleanWithDefaultValue() throws Exception {
		new TestableSelectItems() {
			@Override
			public void test() throws Exception {
				assertThat(getBooleanValue("false", Boolean.TRUE), is(false));
				assertThat(getBooleanValue("true", Boolean.FALSE), is(true));
				assertThat(getBooleanValue("xxx", Boolean.TRUE), is(false));
				assertThat(getBooleanValue(Boolean.FALSE, Boolean.TRUE), is(false));
				assertThat(getBooleanValue(Boolean.TRUE, Boolean.FALSE), is(true));
				assertThat(getBooleanValue(null, Boolean.TRUE), is(true));
				assertThat(getBooleanValue(null, Boolean.FALSE), is(false));
			}
		}.test();
	}

	@Test
	public void shouldGetStringValue() throws Exception {
		new TestableSelectItems() {
			@Override
			public void test() throws Exception {
				assertThat(getStringValue(new StringBuffer("a")), is("a"));
				assertThat(getStringValue(new Integer(123)), is("123"));
				assertThat(getStringValue(null), is(nullValue()));
			}
		}.test();
	}

	@Test
	public void shouldGetValue() throws Exception {
		final SelectItem value = new SelectItem();
		new TestableSelectItems(value) {
			@Override
			public void test() throws Exception {
				assertThat(getValue(), is(sameInstance((Object) value)));
			}
		}.test();
	}

	@Test
	public void shouldNotSupportRemove() throws Exception {
		Iterator<SelectItem> iterator = newSelectItemsIterator(new SelectItem());
		this.thrown.expect(UnsupportedOperationException.class);
		iterator.remove();
	}

	private Iterator<SelectItem> newSelectItemsIterator(Object value) {
		SelectItems selectItems = new TestableSelectItems(value);
		return selectItems.iterator();
	}

	static class TestableSelectItems extends SelectItems {

		public TestableSelectItems(Object value) {
			super(value);
		}

		public TestableSelectItems() {
			super(null);
		}

		@Override
		protected SelectItem convertToSelectItem(Object value) {
			return new SelectItem(value);
		}

		public void test() throws Exception {
		}
	}

}

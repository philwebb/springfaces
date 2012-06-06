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
package org.springframework.springfaces.expression.el;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link BeanBackedELResolver}.
 * 
 * @author Phillip Webb
 */
public class BeanBackedELResolverTest {

	private BeanBackedELResolver resolver;
	private Object bean;

	@Before
	public void setUp() throws Exception {
		this.bean = new TestBean();
		this.resolver = new TestBeanBackedElResolver();
	}

	@Test
	public void shouldBeReadOnly() throws Exception {
		assertTrue(this.resolver.isReadOnly("string"));
	}

	@Test
	public void shouldBeAvailableOnlyWhenHasBean() throws Exception {
		assertTrue(this.resolver.isAvailable());
		this.bean = null;
		assertFalse(this.resolver.isAvailable());
	}

	@Test
	public void testHandlesAndResolve() throws Exception {
		assertThat(this.resolver.handles("stringValue"), is(true));
		assertThat(this.resolver.get("stringValue"), is(equalTo((Object) "string")));
		assertThat(this.resolver.handles("longValue"), is(true));
		assertThat(this.resolver.get("longValue"), is(equalTo((Object) new Long(12345))));
		assertThat(this.resolver.handles("intValue"), is(true));
		assertThat(this.resolver.get("intValue"), is(equalTo((Object) new Integer(1))));
		assertThat(this.resolver.handles("alias"), is(true));
		assertThat(this.resolver.get("alias"), is(equalTo((Object) "string")));
		assertThat(this.resolver.handles("unmapped"), is(false));
	}

	@Test
	public void testNotMapped() throws Exception {
		assertFalse(this.resolver.handles("missing"));
		assertNull(this.resolver.get("missing"));
	}

	private class TestBeanBackedElResolver extends BeanBackedELResolver {

		public TestBeanBackedElResolver() {
			map("stringValue");
			map("longValue");
			map("intValue");
			map("alias", "stringValue");
		}

		@Override
		protected Object getBean() {
			return BeanBackedELResolverTest.this.bean;
		}
	}

	public static class TestBean {
		private String stringValue = "string";
		private Long longValue = new Long(12345);
		private int intValue = 1;
		private String unmapped = "unmapped";

		public String getStringValue() {
			return this.stringValue;
		}

		public Long getLongValue() {
			return this.longValue;
		}

		public int getIntValue() {
			return this.intValue;
		}

		public String getUnmapped() {
			return this.unmapped;
		}
	}
}

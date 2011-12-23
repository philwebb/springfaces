package org.springframework.springfaces.expression.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
		assertTrue(this.resolver.handles("stringValue"));
		assertEquals("string", this.resolver.get("stringValue"));
		assertTrue(this.resolver.handles("longValue"));
		assertEquals(new Long(12345), this.resolver.get("longValue"));
		assertTrue(this.resolver.handles("intValue"));
		assertEquals(new Integer(1), this.resolver.get("intValue"));
		assertTrue(this.resolver.handles("alias"));
		assertEquals("string", this.resolver.get("alias"));
		assertFalse(this.resolver.handles("unmapped"));
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

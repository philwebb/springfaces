package org.springframework.springfaces.mvc.expression.el;

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
		assertTrue(resolver.isReadOnly("string"));
	}

	@Test
	public void shouldBeAvailableOnlyWhenHasBean() throws Exception {
		assertTrue(resolver.isAvailable());
		this.bean = null;
		assertFalse(resolver.isAvailable());
	}

	@Test
	public void testHandlesAndResolve() throws Exception {
		assertTrue(resolver.handles("stringValue"));
		assertEquals("string", resolver.get("stringValue"));
		assertTrue(resolver.handles("longValue"));
		assertEquals(new Long(12345), resolver.get("longValue"));
		assertTrue(resolver.handles("intValue"));
		assertEquals(new Integer(1), resolver.get("intValue"));
		assertTrue(resolver.handles("alias"));
		assertEquals("string", resolver.get("alias"));
		assertFalse(resolver.handles("unmapped"));
	}

	@Test
	public void testNotMapped() throws Exception {
		assertFalse(resolver.handles("missing"));
		assertNull(resolver.get("missing"));
	}

	private class TestBeanBackedElResolver extends BeanBackedELResolver {

		public TestBeanBackedElResolver() {
			map("stringValue");
			map("longValue");
			map("intValue");
			map("alias", "stringValue");
		}

		protected Object getBean() {
			return bean;
		}
	}

	public static class TestBean {
		private String stringValue = "string";
		private Long longValue = new Long(12345);
		private int intValue = 1;
		private String unmapped = "unmapped";

		public String getStringValue() {
			return stringValue;
		}

		public Long getLongValue() {
			return longValue;
		}

		public int getIntValue() {
			return intValue;
		}

		public String getUnmapped() {
			return unmapped;
		}
	}
}

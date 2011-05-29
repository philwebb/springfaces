package org.springframework.springfaces.mvc.expression.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.PropertyNotWritableException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link AbstractELResolver}.
 * 
 * @author Phillip Webb
 */
public class AbstrctELResolverTest {

	private static final String BASE_OBJECT = "baseObject";
	private static final Object PROPERTY_NAME = "myObject";
	private static final Object MISSING_PROPERTY_NAME = "doesNotExist";
	private static final Long PROPERTY_VALUE = new Long(123);
	private static final Long REPLACED_PROPERTY_VALUE = new Long(321);

	private AbstractELResolver resolver;
	private ELContext elContext;
	private Map<Object, Object> map;
	private Boolean handles;
	private Boolean available;
	private Boolean readOnly;

	@Before
	public void setUp() throws Exception {
		this.map = new HashMap<Object, Object>();
		this.resolver = new MockELResolver();
		this.elContext = mock(ELContext.class);
		this.map.put(PROPERTY_NAME, PROPERTY_VALUE);
		this.handles = null;
		this.available = null;
		this.readOnly = null;
	}

	@Test
	public void shouldGetCommonPropertyType() throws Exception {
		assertEquals(Object.class, resolver.getCommonPropertyType(elContext, null));
		assertEquals(null, resolver.getCommonPropertyType(elContext, BASE_OBJECT));
	}

	@Test
	public void shouldGetFeatureDescriptors() throws Exception {
		assertNull(resolver.getFeatureDescriptors(elContext, null));
		assertNull(resolver.getFeatureDescriptors(elContext, BASE_OBJECT));
	}

	@Test
	public void shouldGetTypeForNonNullBase() throws Exception {
		assertNull(resolver.getType(elContext, BASE_OBJECT, PROPERTY_NAME));
	}

	@Test
	public void shouldGetTypeWhenFound() throws Exception {
		assertEquals(Long.class, resolver.getType(elContext, null, PROPERTY_NAME));
		verify(elContext).setPropertyResolved(true);
	}

	@Test
	public void shouldGetTypeWhenNotFound() throws Exception {
		assertNull(resolver.getType(elContext, null, MISSING_PROPERTY_NAME));
		verify(elContext, never()).setPropertyResolved(anyBoolean());
	}

	@Test
	public void shouldGetValueWhenNonNullBase() throws Exception {
		assertNull(resolver.getValue(elContext, BASE_OBJECT, PROPERTY_NAME));
	}

	@Test
	public void shouldGetValueWhenFound() throws Exception {
		assertEquals(PROPERTY_VALUE, resolver.getValue(elContext, null, PROPERTY_NAME));
		verify(elContext).setPropertyResolved(true);
	}

	@Test
	public void shouldGetValueWhenNotFound() throws Exception {
		assertNull(resolver.getValue(elContext, null, MISSING_PROPERTY_NAME));
		verify(elContext, never()).setPropertyResolved(anyBoolean());
	}

	@Test
	public void shouldNotBeReadOnlyWhenNonNullBase() throws Exception {
		assertFalse(resolver.isReadOnly(elContext, BASE_OBJECT, PROPERTY_NAME));
	}

	@Test
	public void shouldBeReadOnly() throws Exception {
		assertTrue(resolver.isReadOnly(elContext, null, PROPERTY_NAME));
		verify(elContext).setPropertyResolved(true);
	}

	@Test
	public void shouldNotBeReadOnlyWhenNotFound() throws Exception {
		assertFalse(resolver.isReadOnly(elContext, null, MISSING_PROPERTY_NAME));
		verify(elContext, never()).setPropertyResolved(anyBoolean());
	}

	@Test
	public void shouldSetValueOnReadOnlyNonNullBase() throws Exception {
		resolver.setValue(elContext, BASE_OBJECT, PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
		verify(elContext, never()).setPropertyResolved(anyBoolean());
		assertEquals(PROPERTY_VALUE, map.get(PROPERTY_NAME));
	}

	@Test
	public void shouldThrowThenSetValueOnReadOnly() throws Exception {
		try {
			resolver.setValue(elContext, null, PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
			fail();
		} catch (PropertyNotWritableException e) {
			assertEquals("The property myObject is not writable.", e.getMessage());
		}
	}

	@Test
	public void shouldSetValueOnReadOnlyNotFound() throws Exception {
		resolver.setValue(elContext, null, MISSING_PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
		verify(elContext, never()).setPropertyResolved(anyBoolean());
		assertFalse(map.containsKey(MISSING_PROPERTY_NAME));
	}

	@Test
	public void shouldSetValueOnMutableObjectWhenFound() throws Exception {
		readOnly = Boolean.FALSE;
		resolver.setValue(elContext, null, PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
		verify(elContext).setPropertyResolved(true);
		assertEquals(REPLACED_PROPERTY_VALUE, map.get(PROPERTY_NAME));
	}

	@Test
	public void shouldSetValueOnMutableObjectWhenNotFound() throws Exception {
		readOnly = Boolean.FALSE;
		resolver.setValue(elContext, null, MISSING_PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
		verify(elContext, never()).setPropertyResolved(anyBoolean());
		assertNull(map.get(MISSING_PROPERTY_NAME));
	}

	@Test
	public void shouldReturnNullOnMissingProperty() throws Exception {
		handles = Boolean.TRUE;
		assertNull(resolver.getValue(elContext, null, MISSING_PROPERTY_NAME));
		verify(elContext).setPropertyResolved(true);
	}

	@Test
	public void shouldGetNullValueWhenNotAvailable() throws Exception {
		available = Boolean.FALSE;
		assertNull(resolver.getValue(elContext, null, PROPERTY_NAME));
		verify(elContext, never()).setPropertyResolved(anyBoolean());
	}

	private class MockELResolver extends AbstractELResolver {

		protected boolean handles(String property) {
			if (handles == null) {
				return super.handles(property);
			}
			return handles.booleanValue();
		}

		protected boolean isAvailable() {
			if (available == null) {
				return super.isAvailable();
			}
			return available.booleanValue();
		}

		protected boolean isReadOnly(String property) {
			if (readOnly == null) {
				return super.isReadOnly(property);
			}
			return readOnly.booleanValue();
		}

		protected Object get(String property) {
			return map.get(property);
		}

		protected void set(String property, Object value) throws PropertyNotWritableException {
			if (readOnly == null) {
				super.set(property, value);
			}
			map.put(property, value);
		}
	}
}

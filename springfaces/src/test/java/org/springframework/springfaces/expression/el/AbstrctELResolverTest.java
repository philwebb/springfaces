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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.PropertyNotWritableException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link AbstractELResolver}.
 * @author Phillip Webb
 */
@SuppressWarnings("rawtypes")
public class AbstrctELResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String BASE_OBJECT = "baseObject";
	private static final Object PROPERTY_NAME = "myObject";
	private static final Object MISSING_PROPERTY_NAME = "doesNotExist";
	private static final Object PROPERTY_VALUE = new Long(123);
	private static final Object REPLACED_PROPERTY_VALUE = new Long(321);

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
		assertThat(this.resolver.getCommonPropertyType(this.elContext, null), is(equalTo((Class) Object.class)));
		assertThat(this.resolver.getCommonPropertyType(this.elContext, BASE_OBJECT), is(nullValue()));
	}

	@Test
	public void shouldGetFeatureDescriptors() throws Exception {
		assertThat(this.resolver.getFeatureDescriptors(this.elContext, null), is(nullValue()));
		assertThat(this.resolver.getFeatureDescriptors(this.elContext, BASE_OBJECT), is(nullValue()));
	}

	@Test
	public void shouldGetTypeForNonNullBase() throws Exception {
		assertThat(this.resolver.getType(this.elContext, BASE_OBJECT, PROPERTY_NAME), is(nullValue()));
	}

	@Test
	public void shouldGetTypeWhenFound() throws Exception {
		assertThat(this.resolver.getType(this.elContext, null, PROPERTY_NAME), is(equalTo((Class) Long.class)));
		verify(this.elContext).setPropertyResolved(true);
	}

	@Test
	public void shouldGetTypeWhenNotFound() throws Exception {
		assertThat(this.resolver.getType(this.elContext, null, MISSING_PROPERTY_NAME), is(nullValue()));
		verify(this.elContext, never()).setPropertyResolved(anyBoolean());
	}

	@Test
	public void shouldGetValueWhenNonNullBase() throws Exception {
		assertThat(this.resolver.getValue(this.elContext, BASE_OBJECT, PROPERTY_NAME), is(nullValue()));
	}

	@Test
	public void shouldGetValueWhenFound() throws Exception {
		assertThat(this.resolver.getValue(this.elContext, null, PROPERTY_NAME), is(equalTo(PROPERTY_VALUE)));
		verify(this.elContext).setPropertyResolved(true);
	}

	@Test
	public void shouldGetValueWhenNotFound() throws Exception {
		assertThat(this.resolver.getValue(this.elContext, null, MISSING_PROPERTY_NAME), is(nullValue()));
		verify(this.elContext, never()).setPropertyResolved(anyBoolean());
	}

	@Test
	public void shouldNotBeReadOnlyWhenNonNullBase() throws Exception {
		assertThat(this.resolver.isReadOnly(this.elContext, BASE_OBJECT, PROPERTY_NAME), is(false));
	}

	@Test
	public void shouldBeReadOnly() throws Exception {
		assertThat(this.resolver.isReadOnly(this.elContext, null, PROPERTY_NAME), is(true));
		verify(this.elContext).setPropertyResolved(true);
	}

	@Test
	public void shouldNotBeReadOnlyWhenNotFound() throws Exception {
		assertThat(this.resolver.isReadOnly(this.elContext, null, MISSING_PROPERTY_NAME), is(false));
		verify(this.elContext, never()).setPropertyResolved(anyBoolean());
	}

	@Test
	public void shouldSetValueOnReadOnlyNonNullBase() throws Exception {
		this.resolver.setValue(this.elContext, BASE_OBJECT, PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
		verify(this.elContext, never()).setPropertyResolved(anyBoolean());
		assertThat(this.map.get(PROPERTY_NAME), is(equalTo(PROPERTY_VALUE)));
	}

	@Test
	public void shouldThrowThenSetValueOnReadOnly() throws Exception {
		this.thrown.expect(PropertyNotWritableException.class);
		this.thrown.expectMessage("The property myObject is not writable.");
		this.resolver.setValue(this.elContext, null, PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
	}

	@Test
	public void shouldSetValueOnReadOnlyNotFound() throws Exception {
		this.resolver.setValue(this.elContext, null, MISSING_PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
		verify(this.elContext, never()).setPropertyResolved(anyBoolean());
		assertThat(this.map.containsKey(MISSING_PROPERTY_NAME), is(false));
	}

	@Test
	public void shouldSetValueOnMutableObjectWhenFound() throws Exception {
		this.readOnly = Boolean.FALSE;
		this.resolver.setValue(this.elContext, null, PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
		verify(this.elContext).setPropertyResolved(true);
		assertThat(this.map.get(PROPERTY_NAME), is(equalTo(REPLACED_PROPERTY_VALUE)));
	}

	@Test
	public void shouldSetValueOnMutableObjectWhenNotFound() throws Exception {
		this.readOnly = Boolean.FALSE;
		this.resolver.setValue(this.elContext, null, MISSING_PROPERTY_NAME, REPLACED_PROPERTY_VALUE);
		verify(this.elContext, never()).setPropertyResolved(anyBoolean());
		assertThat(this.map.get(MISSING_PROPERTY_NAME), is(nullValue()));
	}

	@Test
	public void shouldReturnNullOnMissingProperty() throws Exception {
		this.handles = Boolean.TRUE;
		assertThat(this.resolver.getValue(this.elContext, null, MISSING_PROPERTY_NAME), is(nullValue()));
		verify(this.elContext).setPropertyResolved(true);
	}

	@Test
	public void shouldGetNullValueWhenNotAvailable() throws Exception {
		this.available = Boolean.FALSE;
		assertThat(this.resolver.getValue(this.elContext, null, PROPERTY_NAME), is(nullValue()));
		verify(this.elContext, never()).setPropertyResolved(anyBoolean());
	}

	private class MockELResolver extends AbstractELResolver {

		@Override
		protected boolean handles(String property) {
			if (AbstrctELResolverTest.this.handles == null) {
				return super.handles(property);
			}
			return AbstrctELResolverTest.this.handles.booleanValue();
		}

		@Override
		protected boolean isAvailable() {
			if (AbstrctELResolverTest.this.available == null) {
				return super.isAvailable();
			}
			return AbstrctELResolverTest.this.available.booleanValue();
		}

		@Override
		protected boolean isReadOnly(String property) {
			if (AbstrctELResolverTest.this.readOnly == null) {
				return super.isReadOnly(property);
			}
			return AbstrctELResolverTest.this.readOnly.booleanValue();
		}

		@Override
		protected Object get(String property) {
			return AbstrctELResolverTest.this.map.get(property);
		}

		@Override
		protected void set(String property, Object value) throws PropertyNotWritableException {
			if (AbstrctELResolverTest.this.readOnly == null) {
				super.set(property, value);
			}
			AbstrctELResolverTest.this.map.put(property, value);
		}
	}
}

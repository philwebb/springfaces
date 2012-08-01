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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link ELResolverDecorator}.
 * 
 * @author Phillip Webb
 */
public class ELResolverDecoratorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ELResolver decorated;

	@Mock
	private ELResolverDecorator decorator;

	@Mock
	private ELContext context;

	@Mock
	private Object base;

	@Mock
	private Object property;

	@Mock
	private Object method;

	private Class<?>[] paramTypes = new Class<?>[] { Object.class };

	private Object[] params = new Object[] { new Object() };

	@Mock
	private Object value;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.decorator = new ELResolverDecorator(this.decorated) {
		};
	}

	@Test
	public void shouldNeedDelegateResolver() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Resolver must not be null");
		new ELResolverDecorator(null) {
		};
	}

	@Test
	public void shouldDecorateGetValue() throws Exception {
		given(this.decorated.getValue(this.context, this.base, this.property)).willReturn(this.value);
		Object actual = this.decorator.getValue(this.context, this.base, this.property);
		assertThat(actual, is(this.value));
		verify(this.decorated).getValue(this.context, this.base, this.property);
	}

	@Test
	public void shouldDecorateInvoke() throws Exception {
		given(this.decorated.invoke(this.context, this.base, this.method, this.paramTypes, this.params)).willReturn(
				this.value);
		Object actual = this.decorator.invoke(this.context, this.base, this.method, this.paramTypes, this.params);
		assertThat(actual, is(this.value));
		verify(this.decorated).invoke(this.context, this.base, this.method, this.paramTypes, this.params);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void shouldDecorateGetType() throws Exception {
		Class<?> type = Object.class;
		given(this.decorated.getType(this.context, this.base, this.property)).willReturn((Class) type);
		Class<?> actual = this.decorator.getType(this.context, this.base, this.property);
		assertThat(actual, is(type));
		verify(this.decorated).getType(this.context, this.base, this.property);
	}

	@Test
	public void shouldDecorateSetValue() throws Exception {
		this.decorator.setValue(this.context, this.base, this.property, this.value);
		verify(this.decorated).setValue(this.context, this.base, this.property, this.value);
	}

	@Test
	public void shouldDecorateIsReadOnly() throws Exception {
		boolean readOnly = false;
		given(this.decorated.isReadOnly(this.context, this.base, this.property)).willReturn(readOnly);
		boolean actual = this.decorator.isReadOnly(this.context, this.base, this.property);
		assertThat(actual, is(readOnly));
		verify(this.decorated).isReadOnly(this.context, this.base, this.property);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldDecorateGetFeatureDescriptors() throws Exception {
		Iterator<FeatureDescriptor> featureDescriptor = mock(Iterator.class);
		given(this.decorated.getFeatureDescriptors(this.context, this.base)).willReturn(featureDescriptor);
		Iterator<FeatureDescriptor> actual = this.decorator.getFeatureDescriptors(this.context, this.base);
		assertThat(actual, is(featureDescriptor));
		verify(this.decorated).getFeatureDescriptors(this.context, this.base);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void shouldDecorateGetCommonPropertyType() throws Exception {
		Class<?> commonPropertyType = Object.class;
		given(this.decorated.getCommonPropertyType(this.context, this.base)).willReturn((Class) commonPropertyType);
		Class<?> actual = this.decorator.getCommonPropertyType(this.context, this.base);
		assertThat(actual, is(commonPropertyType));
		verify(this.decorated).getCommonPropertyType(this.context, this.base);
	}

}

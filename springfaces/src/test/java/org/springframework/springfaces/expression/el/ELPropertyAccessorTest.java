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
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.expression.EvaluationContext;

/**
 * Tests for {@link ELPropertyAccessor}.
 * @author Phillip Webb
 */
public class ELPropertyAccessorTest {

	private ELPropertyAccessor elPropertyAccessor;
	private ELContext elContext;
	private ELResolver elResolver;
	private EvaluationContext context = mock(EvaluationContext.class);
	private Object target = new Object();
	private String name = "name";
	private Object value = new Object();
	private Object resolveBase;
	private Object resolveProperty;

	@Before
	public void setup() {
		this.elContext = mock(ELContext.class);
		this.elResolver = mock(ELResolver.class);
		given(this.elContext.getELResolver()).willReturn(this.elResolver);
		this.elPropertyAccessor = new ELPropertyAccessor() {
			@Override
			protected ELContext getElContext(EvaluationContext context, Object target) {
				return ELPropertyAccessorTest.this.elContext;
			}

			@Override
			protected Object getResolveBase(EvaluationContext context, Object target, String name) {
				if (ELPropertyAccessorTest.this.resolveBase != null) {
					return ELPropertyAccessorTest.this.resolveBase;
				}
				return super.getResolveBase(context, target, name);
			};

			@Override
			protected Object getResolveProperty(EvaluationContext context, Object target, String name) {
				if (ELPropertyAccessorTest.this.resolveProperty != null) {
					return ELPropertyAccessorTest.this.resolveProperty;
				}
				return super.getResolveProperty(context, target, name);

			};
		};
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void willResolve(Object base, Object property, Object value) {
		given(this.elResolver.getType(this.elContext, base, property)).willReturn((Class) value.getClass());
		given(this.elResolver.getValue(this.elContext, base, property)).willReturn(value);
		given(this.elContext.isPropertyResolved()).willReturn(true);
	}

	@Test
	public void shouldNotReadIfNotInEl() throws Exception {
		assertThat(this.elPropertyAccessor.canRead(this.context, this.target, this.name), is(false));
		assertThat(this.elPropertyAccessor.read(this.context, this.target, this.name), is(nullValue()));
	}

	@Test
	public void shouldReadIfInEl() throws Exception {
		willResolve(null, this.name, this.value);
		assertThat(this.elPropertyAccessor.canRead(this.context, this.target, this.name), is(true));
		assertThat(this.elPropertyAccessor.read(this.context, this.target, this.name).getValue(),
				is(sameInstance(this.value)));
	}

	@Test
	public void shouldNotWrite() throws Exception {
		assertThat(this.elPropertyAccessor.canWrite(this.context, this.target, this.name), is(false));
		this.elPropertyAccessor.write(this.context, this.target, this.name, this.value);
	}

	@Test
	public void shouldDefaultToBeanExpressionContext() throws Exception {
		assertThat(
				Arrays.equals(new Class<?>[] { BeanExpressionContext.class },
						this.elPropertyAccessor.getSpecificTargetClasses()), is(true));
	}

	@Test
	public void shouldSupportResolveOverrides() throws Exception {
		this.resolveBase = new Object();
		this.resolveProperty = "changed";
		Object expected = new Object();
		willResolve(null, this.name, this.value);
		willResolve(this.resolveBase, this.resolveProperty, expected);
		assertThat(this.elPropertyAccessor.canRead(this.context, this.target, this.name), is(true));
		assertThat(this.elPropertyAccessor.read(this.context, this.target, this.name).getValue(),
				is(sameInstance(expected)));
	}

	@Test
	public void shouldWorkWithoutElContext() throws Exception {
		this.elContext = null;
		assertThat(this.elPropertyAccessor.canRead(this.context, this.target, this.name), is(false));
		assertThat(this.elPropertyAccessor.read(this.context, this.target, this.name), is(nullValue()));
	}
}

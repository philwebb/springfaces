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

import java.util.Locale;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link ELContextDecorator}.
 * 
 * @author Phillip Webb
 */
public class ELContextDecoratorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ELContext decorated;

	private ELContextDecorator decorator;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.decorator = new ELContextDecorator(this.decorated) {
		};
	}

	@Test
	public void shouldNeedDelegateContext() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("ELContext must not be null");
		new ELContextDecorator(null) {
		};
	}

	@Test
	public void shouldDecorateSetPropertyResolved() throws Exception {
		this.decorator.setPropertyResolved(true);
		verify(this.decorated).setPropertyResolved(true);
	}

	@Test
	public void shouldDecorateIsPropertyResolved() throws Exception {
		given(this.decorated.isPropertyResolved()).willReturn(true);
		boolean actual = this.decorator.isPropertyResolved();
		assertThat(actual, is(true));
		verify(this.decorated).isPropertyResolved();
	}

	@Test
	public void shouldDecoratePutContext() throws Exception {
		Class key = Object.class;
		Object contextObject = new Object();
		this.decorator.putContext(key, contextObject);
		verify(this.decorated).putContext(key, contextObject);
	}

	@Test
	public void shouldDecorateGetContext() throws Exception {
		Class key = Object.class;
		Object contextObject = new Object();
		given(this.decorated.getContext(key)).willReturn(contextObject);
		Object actual = this.decorator.getContext(key);
		assertThat(actual, is(contextObject));
		verify(this.decorated).getContext(key);
	}

	@Test
	public void shouldDecorateSetLocale() throws Exception {
		Locale locale = Locale.CANADA;
		this.decorator.setLocale(locale);
		verify(this.decorated).setLocale(locale);
	}

	@Test
	public void shouldDecorateGetELResolver() throws Exception {
		ELResolver elResolver = mock(ELResolver.class);
		given(this.decorated.getELResolver()).willReturn(elResolver);
		ELResolver actual = this.decorator.getELResolver();
		assertThat(actual, is(elResolver));
		verify(this.decorated).getELResolver();
	}

	@Test
	public void shouldDecorateGetFunctionMapper() throws Exception {
		FunctionMapper funcationMapper = mock(FunctionMapper.class);
		given(this.decorated.getFunctionMapper()).willReturn(funcationMapper);
		FunctionMapper actual = this.decorator.getFunctionMapper();
		assertThat(actual, is(funcationMapper));
		verify(this.decorated).getFunctionMapper();
	}

	@Test
	public void shouldDecorateGetVariableMapper() throws Exception {
		VariableMapper variableMapper = mock(VariableMapper.class);
		given(this.decorated.getVariableMapper()).willReturn(variableMapper);
		VariableMapper actual = this.decorator.getVariableMapper();
		assertThat(actual, is(variableMapper));
		verify(this.decorated).getVariableMapper();
	}

}

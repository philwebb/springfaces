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
package org.springframework.springfaces.mvc.method.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockMethodParameter;

import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Tests for {@link ImplicitObjectMethodArgumentResolver}.
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class ImplicitObjectMethodArgumentResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private MockImplicitObjectMethodArgumentResolver resolver = new MockImplicitObjectMethodArgumentResolver();

	@Mock
	private Callable<Type> call;

	@Mock
	private Callable<Boolean> condition;

	@Mock
	private ModelAndViewContainer mavContainer;

	@Mock
	private NativeWebRequest webRequest;

	@Mock
	private WebDataBinderFactory binderFactory;

	@Test
	public void shouldSupportOnType() throws Exception {
		this.resolver.add(Type.class, this.call);
		assertThat(this.resolver.supportsParameter(mockMethodParameter(SuperType.class)), is(true));
		assertThat(this.resolver.supportsParameter(mockMethodParameter(Type.class)), is(true));
		assertThat(this.resolver.supportsParameter(mockMethodParameter(TypeInterface.class)), is(true));
		assertThat(this.resolver.supportsParameter(mockMethodParameter(SubType.class)), is(false));
	}

	@Test
	public void shouldSupportOnTypeMatchAndCondition() throws Exception {
		given(this.condition.call()).willReturn(true);
		this.resolver.add(Type.class, this.condition, this.call);
		assertThat(this.resolver.supportsParameter(mockMethodParameter(SuperType.class)), is(true));
		assertThat(this.resolver.supportsParameter(mockMethodParameter(Type.class)), is(true));
		assertThat(this.resolver.supportsParameter(mockMethodParameter(TypeInterface.class)), is(true));
		reset(this.condition);
		given(this.condition.call()).willReturn(false);
		assertThat(this.resolver.supportsParameter(mockMethodParameter(SuperType.class)), is(false));
		assertThat(this.resolver.supportsParameter(mockMethodParameter(Type.class)), is(false));
		assertThat(this.resolver.supportsParameter(mockMethodParameter(TypeInterface.class)), is(false));
	}

	@Test
	public void shouldRethrowOnConditionException() throws Exception {
		Exception ex = new Exception();
		given(this.condition.call()).willThrow(ex);
		this.resolver.add(Type.class, this.condition, this.call);
		try {
			this.resolver.supportsParameter(mockMethodParameter(Type.class));
			fail("did not throw");
		} catch (RuntimeException e) {
			assertThat(e.getCause(), is(sameInstance((Throwable) ex)));
		}
	}

	@Test
	public void shouldResolveUsingCall() throws Exception {
		Type resolved = mock(Type.class);
		given(this.call.call()).willReturn(resolved);
		this.resolver.add(Type.class, this.call);
		assertThat(this.resolver.resolveArgument(mockMethodParameter(Type.class), this.mavContainer, this.webRequest,
				this.binderFactory), is(sameInstance((Object) resolved)));
	}

	@Test
	public void shouldRethrowResolveExceptions() throws Exception {
		Exception ex = new Exception();
		given(this.call.call()).willThrow(ex);
		this.resolver.add(Type.class, this.call);
		this.thrown.expect(equalTo(ex));
		this.resolver.resolveArgument(mockMethodParameter(Type.class), this.mavContainer, this.webRequest,
				this.binderFactory);
	}

	private static class MockImplicitObjectMethodArgumentResolver extends ImplicitObjectMethodArgumentResolver {
	}

	private static class SuperType {
	}

	private static interface TypeInterface {
	}

	private static class Type extends SuperType implements TypeInterface {
	}

	private static class SubType extends Type {
	}

}

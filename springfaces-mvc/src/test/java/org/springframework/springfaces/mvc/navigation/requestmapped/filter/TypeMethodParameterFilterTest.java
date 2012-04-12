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
package org.springframework.springfaces.mvc.navigation.requestmapped.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Tests for {@link TypeMethodParameterFilter}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeMethodParameterFilterTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private NativeWebRequest request;

	@Test
	public void shouldRequireIgnoredTypes() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("IgnoredTypes must not be null");
		new TypeMethodParameterFilter((Class<?>[]) null);
	}

	@Test
	public void shouldIgnoreTypes() throws Exception {
		Method method = getMethod();
		TypeMethodParameterFilter f = new TypeMethodParameterFilter(T1.class, T2.class);
		assertTrue(f.isFiltered(this.request, new MethodParameter(method, 0)));
		assertTrue(f.isFiltered(this.request, new MethodParameter(method, 1)));
		assertTrue(f.isFiltered(this.request, new MethodParameter(method, 2)));
		assertFalse(f.isFiltered(this.request, new MethodParameter(method, 3)));
	}

	private Method getMethod() {
		return ReflectionUtils.findMethod(C.class, "m", T1.class, T1X.class, T2.class, T3.class);
	}

	public static class T1 {
	}

	public static class T1X extends T1 {
	}

	public static class T2 {
	}

	public static class T3 {
	}

	public static class C {
		public void m(T1 p1, T1X p2, T2 p3, T3 p4) {
		}
	}

}

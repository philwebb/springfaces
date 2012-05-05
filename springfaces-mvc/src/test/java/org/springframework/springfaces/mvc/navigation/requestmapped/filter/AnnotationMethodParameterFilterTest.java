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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
 * Tests for {@link AnnotationMethodParameterFilter}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationMethodParameterFilterTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private NativeWebRequest request;

	@Test
	public void shouldRequireIgnoredAnnotations() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("IgnoredAnnotations must not be null");
		new AnnotationMethodParameterFilter((Class<?>[]) null);
	}

	@Test
	public void shouldFilterIgnoredAnnotations() throws Exception {
		Method method = ReflectionUtils
				.findMethod(C.class, "m", Object.class, Object.class, Object.class, Object.class);
		AnnotationMethodParameterFilter f = new AnnotationMethodParameterFilter(A1.class, A3.class);
		assertTrue(f.matches(this.request, new MethodParameter(method, 0)));
		assertFalse(f.matches(this.request, new MethodParameter(method, 1)));
		assertTrue(f.matches(this.request, new MethodParameter(method, 2)));
		assertTrue(f.matches(this.request, new MethodParameter(method, 3)));

	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface A1 {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface A2 {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface A3 {
	}

	public static class C {
		public void m(@A1 Object p1, @A2 Object p2, @A3 Object p3, @A1 @A2 @A3 Object p4) {
		}
	}

}

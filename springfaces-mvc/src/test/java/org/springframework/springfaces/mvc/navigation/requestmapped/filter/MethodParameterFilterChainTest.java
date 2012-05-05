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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Tests for {@link MethodParameterFilterChain}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class MethodParameterFilterChainTest {

	@Mock
	private NativeWebRequest request;

	@Mock
	MethodParameter methodParameter;

	@Test
	public void shouldWorkWithNullChain() throws Exception {
		MethodParameterFilterChain c = new MethodParameterFilterChain((MethodParameterFilter[]) null);
		assertFalse(c.matches(this.request, this.methodParameter));
	}

	@Test
	public void shouldCallChainStopingAtFirstFiltered() throws Exception {

		MethodParameterFilter f1 = mock(MethodParameterFilter.class);
		MethodParameterFilter f2 = mock(MethodParameterFilter.class);
		MethodParameterFilter f3 = mock(MethodParameterFilter.class);
		given(f2.matches(this.request, this.methodParameter)).willReturn(true);
		MethodParameterFilterChain c = new MethodParameterFilterChain(f1, f2, f3);
		assertTrue(c.matches(this.request, this.methodParameter));
		InOrder ordered = inOrder(f1, f2, f3);
		ordered.verify(f1).matches(this.request, this.methodParameter);
		ordered.verify(f2).matches(this.request, this.methodParameter);
		ordered.verify(f3, never()).matches(this.request, this.methodParameter);
	}
}

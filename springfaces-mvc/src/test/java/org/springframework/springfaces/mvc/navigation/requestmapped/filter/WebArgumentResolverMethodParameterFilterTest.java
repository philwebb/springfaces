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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Tests for {@link WebArgumentResolverMethodParameterFilter}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class WebArgumentResolverMethodParameterFilterTest {

	@Mock
	private MethodParameter methodParameter;

	@Mock
	private NativeWebRequest request;

	@Test
	public void shouldAcceptNullWebArgumentResolver() throws Exception {
		WebArgumentResolverMethodParameterFilter f = new WebArgumentResolverMethodParameterFilter(
				(WebArgumentResolver[]) null);
		assertFalse(f.matches(this.request, this.methodParameter));
	}

	@Test
	public void shouldFilterIfWebArgumentResolved() throws Exception {
		WebArgumentResolver w1 = mock(WebArgumentResolver.class);
		WebArgumentResolver w2 = mock(WebArgumentResolver.class);
		WebArgumentResolver w3 = mock(WebArgumentResolver.class);
		given(w1.resolveArgument(this.methodParameter, this.request)).willReturn(WebArgumentResolver.UNRESOLVED);
		given(w3.resolveArgument(this.methodParameter, this.request)).willReturn(WebArgumentResolver.UNRESOLVED);
		WebArgumentResolverMethodParameterFilter f = new WebArgumentResolverMethodParameterFilter(w1, w2, w3);
		assertThat(f.matches(this.request, this.methodParameter), is(true));
		InOrder ordered = inOrder(w1, w2, w3);
		ordered.verify(w1).resolveArgument(this.methodParameter, this.request);
		ordered.verify(w2).resolveArgument(this.methodParameter, this.request);
		ordered.verify(w3, never()).resolveArgument(this.methodParameter, this.request);
	}

}

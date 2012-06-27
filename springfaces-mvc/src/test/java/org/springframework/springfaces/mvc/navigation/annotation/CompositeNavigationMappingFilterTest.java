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
package org.springframework.springfaces.mvc.navigation.annotation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.mvc.navigation.NavigationContext;

/**
 * Tests for {@link CompositeNavigationMappingFilter}.
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class CompositeNavigationMappingFilterTest {

	@Rule
	public ExpectedException thown = ExpectedException.none();

	@Mock
	private NavigationContext context;

	@Test
	public void shouldNeedArray() throws Exception {
		this.thown.expect(IllegalArgumentException.class);
		this.thown.expectMessage("Filters must not be null");
		new CompositeNavigationMappingFilter((NavigationMappingFilter[]) null);
	}

	@Test
	public void shouldNeedNoNullElements() throws Exception {
		this.thown.expect(IllegalArgumentException.class);
		this.thown.expectMessage("Filters must not contain null elements");
		new CompositeNavigationMappingFilter((NavigationMappingFilter) null);
	}

	@Test
	public void shouldMatchIfAllMatch() throws Exception {
		NavigationMappingFilter f1 = mock(NavigationMappingFilter.class);
		NavigationMappingFilter f2 = mock(NavigationMappingFilter.class);
		NavigationMappingFilter f3 = mock(NavigationMappingFilter.class);
		given(f1.matches(this.context)).willReturn(true);
		given(f2.matches(this.context)).willReturn(true);
		given(f3.matches(this.context)).willReturn(true);
		CompositeNavigationMappingFilter composite = new CompositeNavigationMappingFilter(f1, f2, f3);
		assertThat(composite.matches(this.context), is(true));
	}

	@Test
	public void shouldNotMatchIfAnyDoesNotMatch() throws Exception {
		NavigationMappingFilter f1 = mock(NavigationMappingFilter.class);
		NavigationMappingFilter f2 = mock(NavigationMappingFilter.class);
		NavigationMappingFilter f3 = mock(NavigationMappingFilter.class);
		given(f1.matches(this.context)).willReturn(true);
		given(f2.matches(this.context)).willReturn(false);
		given(f3.matches(this.context)).willReturn(true);
		CompositeNavigationMappingFilter composite = new CompositeNavigationMappingFilter(f1, f2, f3);
		assertThat(composite.matches(this.context), is(false));
	}

}

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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.util.ReflectionUtils;

/**
 * Tests for {@link NavigationMappingMethod}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationMappingMethodTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Bean bean = new Bean();

	@Mock
	private NavigationContext context;

	private String beanName = "beanName";

	@Test
	public void shouldNeedBeanName() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("BeanName must not be null");
		new NavigationMappingMethod(null, Bean.class, Bean.defaults, true);
	}

	@Test
	public void shouldNeedBeanType() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("BeanType must not be null");
		new NavigationMappingMethod(this.beanName, null, Bean.defaults, true);
	}

	@Test
	public void shouldNeedMethod() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Method must not be null");
		new NavigationMappingMethod(this.beanName, Bean.class, null, true);
	}

	@Test
	public void shouldNeedAnnotationOnMethod() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to find @NavigationMapping annotation on method Bean.noAnnotation");
		new NavigationMappingMethod(this.beanName, Bean.class, Bean.noAnnotation, true);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldGetDetails() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.defaults, true);
		assertThat(o.getBeanName(), is(equalTo(this.beanName)));
		assertThat(o.getBeanType(), is(equalTo((Class) Bean.class)));
		assertThat(o.getMethod(), is(sameInstance(Bean.defaults)));
	}

	@Test
	public void shouldMatchMethodWhenNoOutcome() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.defaults, true);
		given(this.context.getController()).willReturn(this.bean);
		given(this.context.getOutcome()).willReturn("noMatch", "defaults");
		assertThat(o.canResolve(this.context), is(false));
		assertThat(o.canResolve(this.context), is(true));
	}

	@Test
	public void shouldIgnoreMethodPrefixes() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.onDefaults, true);
		given(this.context.getController()).willReturn(this.bean);
		given(this.context.getOutcome()).willReturn("noMatch", "defaults");
		assertThat(o.canResolve(this.context), is(false));
		assertThat(o.canResolve(this.context), is(true));
	}

	@Test
	public void shouldIgnoreMethodPrefixOnOwn() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.on, true);
		given(this.context.getController()).willReturn(this.bean);
		given(this.context.getOutcome()).willReturn("noMatch", "on");
		assertThat(o.canResolve(this.context), is(false));
		assertThat(o.canResolve(this.context), is(true));
	}

	@Test
	public void shouldSupportSpecifiedOutcomes() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.specified, true);
		given(this.context.getController()).willReturn(this.bean);
		given(this.context.getOutcome()).willReturn("zero", "one", "two", "three", "four");
		assertThat(o.canResolve(this.context), is(false));
		assertThat(o.canResolve(this.context), is(true));
		assertThat(o.canResolve(this.context), is(true));
		assertThat(o.canResolve(this.context), is(true));
		assertThat(o.canResolve(this.context), is(false));
	}

	@Test
	public void shouldIgnoreFromActionWhenNotSpecified() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.onDefaults, true);
		given(this.context.getController()).willReturn(this.bean);
		given(this.context.getOutcome()).willReturn("defaults");
		given(this.context.getFromAction()).willReturn("doesnotmatter");
		assertThat(o.canResolve(this.context), is(true));
	}

	@Test
	public void shouldSupportFromAction() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.fromAction, true);
		given(this.context.getController()).willReturn(this.bean);
		given(this.context.getOutcome()).willReturn("fromAction");
		given(this.context.getFromAction()).willReturn("noMatch", "#{action}");
		assertThat(o.canResolve(this.context), is(false));
		assertThat(o.canResolve(this.context), is(true));
	}

	@Test
	public void shouldOnlyMatchControllerBean() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.defaults, true);
		Object h1 = new Object();
		Object h2 = new Object();
		given(this.context.getController()).willReturn(this.bean, h1, h2);
		given(this.context.getOutcome()).willReturn("defaults");
		assertThat(o.canResolve(this.context), is(true));
		assertThat(o.canResolve(this.context), is(false));
		assertThat(o.canResolve(this.context), is(false));
	}

	@Test
	public void shouldMatchAllBeans() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.defaults, false);
		Object h1 = new Object();
		Object h2 = new Object();
		given(this.context.getController()).willReturn(this.bean, h1, h2);
		given(this.context.getOutcome()).willReturn("defaults");
		assertThat(o.canResolve(this.context), is(true));
		assertThat(o.canResolve(this.context), is(true));
		assertThat(o.canResolve(this.context), is(true));
	}

	@Test
	public void shouldFailFastOnFilterCreateErrors() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to create filter from class");
		new NavigationMappingMethod(this.beanName, Bean.class, Bean.malformedFilter, true);
	}

	@Test
	public void shouldFilterContext() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(this.beanName, Bean.class, Bean.filter, true);
		given(this.context.getController()).willReturn(this.bean);
		given(this.context.getOutcome()).willReturn("filter");
		assertThat(o.canResolve(this.context), is(true));
		assertThat(o.canResolve(this.context), is(false));
	}

	public static abstract class AbstractFilter implements NavigationMappingFilter {
	}

	public static class Filter implements NavigationMappingFilter {
		public boolean filtered = true;

		public boolean matches(NavigationContext context) {
			boolean rtn = this.filtered;
			this.filtered = !this.filtered;
			return rtn;
		}
	}

	public static class Bean {

		public static Method defaults;
		public static Method noAnnotation;
		public static Method onDefaults;
		public static Method on;
		public static Method specified;
		public static Method fromAction;
		public static Method malformedFilter;
		public static Method filter;

		static {
			noAnnotation = ReflectionUtils.findMethod(Bean.class, "noAnnotation");
			defaults = ReflectionUtils.findMethod(Bean.class, "defaults");
			onDefaults = ReflectionUtils.findMethod(Bean.class, "onDefaults");
			on = ReflectionUtils.findMethod(Bean.class, "on");
			specified = ReflectionUtils.findMethod(Bean.class, "specified");
			fromAction = ReflectionUtils.findMethod(Bean.class, "fromAction");
			malformedFilter = ReflectionUtils.findMethod(Bean.class, "malformedFilter");
			filter = ReflectionUtils.findMethod(Bean.class, "filter");
		}

		public void noAnnotation() {
		}

		@NavigationMapping
		public void defaults() {
		}

		@NavigationMapping
		public void onDefaults() {
		}

		@NavigationMapping
		public void on() {
		}

		@NavigationMapping({ "one", "two", "three" })
		public void specified() {
		}

		@NavigationMapping(fromAction = "#{action}")
		public void fromAction() {
		}

		@NavigationMapping(filter = AbstractFilter.class)
		public void malformedFilter() {
		}

		@NavigationMapping(filter = Filter.class)
		public void filter() {
		}

	}
}

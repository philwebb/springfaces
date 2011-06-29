package org.springframework.springfaces.mvc.navigation.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("BeanName must not be null");
		new NavigationMappingMethod(null, Bean.class, Bean.defaults, true);
	}

	@Test
	public void shouldNeedBeanType() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("BeanType must not be null");
		new NavigationMappingMethod(beanName, null, Bean.defaults, true);
	}

	@Test
	public void shouldNeedMethod() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Method must not be null");
		new NavigationMappingMethod(beanName, Bean.class, null, true);
	}

	@Test
	public void shouldNeedAnnotationOnMethod() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to find @NavigationMapping annotation on method Bean.noAnnotation");
		new NavigationMappingMethod(beanName, Bean.class, Bean.noAnnotation, true);
	}

	@Test
	public void shouldGetDetails() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.defaults, true);
		assertEquals(beanName, o.getBeanName());
		assertEquals(Bean.class, o.getBeanType());
		assertSame(Bean.defaults, o.getMethod());
	}

	@Test
	public void shouldMatchMethodWhenNoOutcome() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.defaults, true);
		given(context.getController()).willReturn(bean);
		given(context.getOutcome()).willReturn("noMatch", "defaults");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldIgnoreMethodPrefixes() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.onDefaults, true);
		given(context.getController()).willReturn(bean);
		given(context.getOutcome()).willReturn("noMatch", "defaults");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldIgnoreMethodPrefixOnOwn() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.on, true);
		given(context.getController()).willReturn(bean);
		given(context.getOutcome()).willReturn("noMatch", "on");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldSupportSpecifiedOutcomes() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.specified, true);
		given(context.getController()).willReturn(bean);
		given(context.getOutcome()).willReturn("zero", "one", "two", "three", "four");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertFalse(o.canResolve(context));
	}

	@Test
	public void shouldIgnoreFromActionWhenNotSpecified() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.onDefaults, true);
		given(context.getController()).willReturn(bean);
		given(context.getOutcome()).willReturn("defaults");
		given(context.getFromAction()).willReturn("doesnotmatter");
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldSupportFromAction() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.fromAction, true);
		given(context.getController()).willReturn(bean);
		given(context.getOutcome()).willReturn("fromAction");
		given(context.getFromAction()).willReturn("noMatch", "#{action}");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldOnlyMatchControllerBean() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.defaults, true);
		Object h1 = new Object();
		Object h2 = new Object();
		given(context.getController()).willReturn(bean, h1, h2);
		given(context.getOutcome()).willReturn("defaults");
		assertTrue(o.canResolve(context));
		assertFalse(o.canResolve(context));
		assertFalse(o.canResolve(context));
	}

	@Test
	public void shouldMatchAllBeans() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.defaults, false);
		Object h1 = new Object();
		Object h2 = new Object();
		given(context.getController()).willReturn(bean, h1, h2);
		given(context.getOutcome()).willReturn("defaults");
		assertTrue(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldFailFastOnFilterCreateErrors() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to create filter from class");
		new NavigationMappingMethod(beanName, Bean.class, Bean.malformedFilter, true);
	}

	@Test
	public void shouldFilterContext() throws Exception {
		NavigationMappingMethod o = new NavigationMappingMethod(beanName, Bean.class, Bean.filter, true);
		given(context.getController()).willReturn(bean);
		given(context.getOutcome()).willReturn("filter");
		assertTrue(o.canResolve(context));
		assertFalse(o.canResolve(context));
	}

	public static abstract class AbstractFilter implements NavigationMappingFilter {
	}

	public static class Filter implements NavigationMappingFilter {
		public boolean filtered = true;

		public boolean matches(NavigationContext context) {
			boolean rtn = filtered;
			filtered = !filtered;
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

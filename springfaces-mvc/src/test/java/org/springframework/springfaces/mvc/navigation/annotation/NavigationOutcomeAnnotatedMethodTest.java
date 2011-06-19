package org.springframework.springfaces.mvc.navigation.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * Tests for {@link NavigationOutcomeAnnotatedMethod}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationOutcomeAnnotatedMethodTest {

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
		new NavigationOutcomeAnnotatedMethod(null, Bean.class, Bean.defaults);
	}

	@Test
	public void shouldNeedBeanType() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("BeanType must not be null");
		new NavigationOutcomeAnnotatedMethod(beanName, null, Bean.defaults);
	}

	@Test
	public void shouldNeedMethod() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Method must not be null");
		new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, null);
	}

	@Test
	public void shouldNeedAnnotationOnMethod() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to find @NavigationMapping annotation on method Bean.noAnnotation");
		new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.noAnnotation);
	}

	@Test
	public void shouldGetDetails() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.defaults);
		assertEquals(beanName, o.getBeanName());
		assertEquals(Bean.class, o.getBeanType());
		assertSame(Bean.defaults, o.getMethod());
	}

	@Test
	public void shouldMatchMethodWhenNoOutcome() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.defaults);
		given(context.getHandler()).willReturn(bean);
		given(context.getOutcome()).willReturn("noMatch", "defaults");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldIgnoreMethodPrefixes() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.onDefaults);
		given(context.getHandler()).willReturn(bean);
		given(context.getOutcome()).willReturn("noMatch", "defaults");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldIgnoreMethodPrefixOnOwn() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.on);
		given(context.getHandler()).willReturn(bean);
		given(context.getOutcome()).willReturn("noMatch", "on");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldSupportSpecifiedOutcomes() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.specified);
		given(context.getHandler()).willReturn(bean);
		given(context.getOutcome()).willReturn("zero", "one", "two", "three", "four");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertFalse(o.canResolve(context));
	}

	@Test
	public void shouldIgnoreFromActionWhenNotSpecified() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.onDefaults);
		given(context.getHandler()).willReturn(bean);
		given(context.getOutcome()).willReturn("defaults");
		given(context.getFromAction()).willReturn("doesnotmatter");
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldSupportFromAction() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.fromAction);
		given(context.getHandler()).willReturn(bean);
		given(context.getOutcome()).willReturn("fromAction");
		given(context.getFromAction()).willReturn("noMatch", "#{action}");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldSupportAllHandlers() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class,
				Bean.allHandlers);
		Object h1 = new Object();
		Object h2 = new Object();
		given(context.getHandler()).willReturn(bean, h1, h2);
		given(context.getOutcome()).willReturn("allHandlers");
		assertTrue(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertTrue(o.canResolve(context));
	}

	@Test
	public void shouldSupportThisHandler() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class, Bean.defaults);
		Object h1 = new Object();
		Object h2 = new Object();
		given(context.getHandler()).willReturn(bean, h1, h2);
		given(context.getOutcome()).willReturn("defaults");
		assertTrue(o.canResolve(context));
		assertFalse(o.canResolve(context));
		assertFalse(o.canResolve(context));
	}

	@Test
	public void shouldSupportSpecifiedHandlers() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class,
				Bean.specificHandler);
		Object h1 = new CustomHandler();
		Object h2 = new Object();
		given(context.getHandler()).willReturn(bean, h1, h2);
		given(context.getOutcome()).willReturn("specificHandler");
		assertFalse(o.canResolve(context));
		assertTrue(o.canResolve(context));
		assertFalse(o.canResolve(context));
	}

	@Test
	public void shouldSupportSpecifiedHandlerWhenHandlerMethod() throws Exception {
		NavigationOutcomeAnnotatedMethod o = new NavigationOutcomeAnnotatedMethod(beanName, Bean.class,
				Bean.specificHandler);
		HandlerMethod h1 = mock(HandlerMethod.class);
		given(h1.createWithResolvedBean()).willReturn(h1);
		given(h1.getBean()).willReturn(new CustomHandler());
		given(context.getHandler()).willReturn(h1);
		given(context.getOutcome()).willReturn("specificHandler");
		assertTrue(o.canResolve(context));
	}

	public static class CustomHandler {
	}

	public static class Bean {

		public static Method defaults;
		public static Method noAnnotation;
		public static Method onDefaults;
		public static Method on;
		public static Method specified;
		public static Method fromAction;
		public static Method allHandlers;
		public static Method specificHandler;

		static {
			noAnnotation = ReflectionUtils.findMethod(Bean.class, "noAnnotation");
			defaults = ReflectionUtils.findMethod(Bean.class, "defaults");
			onDefaults = ReflectionUtils.findMethod(Bean.class, "onDefaults");
			on = ReflectionUtils.findMethod(Bean.class, "on");
			specified = ReflectionUtils.findMethod(Bean.class, "specified");
			fromAction = ReflectionUtils.findMethod(Bean.class, "fromAction");
			allHandlers = ReflectionUtils.findMethod(Bean.class, "allHandlers");
			specificHandler = ReflectionUtils.findMethod(Bean.class, "specificHandler");
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

		@NavigationMapping(handlers = {})
		public void allHandlers() {
		}

		@NavigationMapping(handlers = { CustomHandler.class })
		public void specificHandler() {
		}
	}

}

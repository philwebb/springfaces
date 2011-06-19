package org.springframework.springfaces.mvc;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * Tests for {@link HandlerUtils}.
 * 
 * @author Phillip Webb
 */
public class HandlerUtilsTest {

	private Method method = ReflectionUtils.findMethod(getClass(), "method");

	public void method() {
	}

	@Test
	public void shouldSupportNullHandlerBean() throws Exception {
		assertNull(HandlerUtils.getHandlerBean(null));
	}

	@Test
	public void shouldReturnHandlerIfNotHandlerMethod() throws Exception {
		Object handler = new Object();
		assertSame(handler, HandlerUtils.getHandlerBean(handler));
	}

	@Test
	public void shouldUnwrapHandlerMethodFromBeanName() throws Exception {
		BeanFactory beanFactory = mock(BeanFactory.class);
		Object handler = new Object();
		given(beanFactory.containsBean("beanName")).willReturn(true);
		given(beanFactory.getBean("beanName")).willReturn(handler);
		HandlerMethod handlerMethod = new HandlerMethod("beanName", beanFactory, method);
		assertSame(handler, HandlerUtils.getHandlerBean(handlerMethod));
	}

	@Test
	public void shouldUnwrapHandlerMethod() throws Exception {
		Object handler = new Object();
		HandlerMethod handlerMethod = new HandlerMethod(handler, method);
		assertSame(handler, HandlerUtils.getHandlerBean(handlerMethod));
	}
}

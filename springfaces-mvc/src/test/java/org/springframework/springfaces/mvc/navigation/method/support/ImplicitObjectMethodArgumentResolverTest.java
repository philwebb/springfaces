package org.springframework.springfaces.mvc.navigation.method.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Tests for {@link ImplicitObjectMethodArgumentResolver}.
 * 
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
		resolver.add(Type.class, call);
		assertTrue(resolver.supportsParameter(mockParameterType(SuperType.class)));
		assertTrue(resolver.supportsParameter(mockParameterType(Type.class)));
		assertTrue(resolver.supportsParameter(mockParameterType(TypeInterface.class)));
		assertFalse(resolver.supportsParameter(mockParameterType(SubType.class)));
	}

	@Test
	public void shouldSupportOnTypeMatchAndCondition() throws Exception {
		given(condition.call()).willReturn(true);
		resolver.add(Type.class, condition, call);
		assertTrue(resolver.supportsParameter(mockParameterType(SuperType.class)));
		assertTrue(resolver.supportsParameter(mockParameterType(Type.class)));
		assertTrue(resolver.supportsParameter(mockParameterType(TypeInterface.class)));
		reset(condition);
		given(condition.call()).willReturn(false);
		assertFalse(resolver.supportsParameter(mockParameterType(SuperType.class)));
		assertFalse(resolver.supportsParameter(mockParameterType(Type.class)));
		assertFalse(resolver.supportsParameter(mockParameterType(TypeInterface.class)));
	}

	@Test
	public void shouldRethrowOnConditionException() throws Exception {
		Exception ex = new Exception();
		given(condition.call()).willThrow(ex);
		resolver.add(Type.class, condition, call);
		try {
			resolver.supportsParameter(mockParameterType(Type.class));
			fail("did not throw");
		} catch (RuntimeException e) {
			assertSame(ex, e.getCause());
		}
	}

	@Test
	public void shouldResolveUsingCall() throws Exception {
		Type resolved = mock(Type.class);
		given(call.call()).willReturn(resolved);
		resolver.add(Type.class, call);
		assertSame(resolved,
				resolver.resolveArgument(mockParameterType(Type.class), mavContainer, webRequest, binderFactory));
	}

	@Test
	public void shouldRethrowResolveExceptions() throws Exception {
		Exception ex = new Exception();
		given(call.call()).willThrow(ex);
		resolver.add(Type.class, call);
		thrown.expect(equalTo(ex));
		resolver.resolveArgument(mockParameterType(Type.class), mavContainer, webRequest, binderFactory);
	}

	@SuppressWarnings("rawtypes")
	private MethodParameter mockParameterType(Class parameterType) {
		MethodParameter methodParameter = mock(MethodParameter.class);
		given(methodParameter.getParameterType()).willReturn(parameterType);
		return methodParameter;
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

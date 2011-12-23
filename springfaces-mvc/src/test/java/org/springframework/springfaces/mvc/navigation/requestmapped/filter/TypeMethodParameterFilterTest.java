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

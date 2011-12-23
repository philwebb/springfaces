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
		assertFalse(f.isFiltered(this.request, this.methodParameter));
	}

	@Test
	public void shouldFilterIfWebArgumentResolved() throws Exception {
		WebArgumentResolver w1 = mock(WebArgumentResolver.class);
		WebArgumentResolver w2 = mock(WebArgumentResolver.class);
		WebArgumentResolver w3 = mock(WebArgumentResolver.class);
		given(w1.resolveArgument(this.methodParameter, this.request)).willReturn(WebArgumentResolver.UNRESOLVED);
		given(w3.resolveArgument(this.methodParameter, this.request)).willReturn(WebArgumentResolver.UNRESOLVED);
		WebArgumentResolverMethodParameterFilter f = new WebArgumentResolverMethodParameterFilter(w1, w2, w3);
		assertTrue(f.isFiltered(this.request, this.methodParameter));
		InOrder ordered = inOrder(w1, w2, w3);
		ordered.verify(w1).resolveArgument(this.methodParameter, this.request);
		ordered.verify(w2).resolveArgument(this.methodParameter, this.request);
		ordered.verify(w3, never()).resolveArgument(this.methodParameter, this.request);
	}

}

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
		assertFalse(c.isFiltered(this.request, this.methodParameter));
	}

	@Test
	public void shouldCallChainStopingAtFirstFiltered() throws Exception {

		MethodParameterFilter f1 = mock(MethodParameterFilter.class);
		MethodParameterFilter f2 = mock(MethodParameterFilter.class);
		MethodParameterFilter f3 = mock(MethodParameterFilter.class);
		given(f2.isFiltered(this.request, this.methodParameter)).willReturn(true);
		MethodParameterFilterChain c = new MethodParameterFilterChain(f1, f2, f3);
		assertTrue(c.isFiltered(this.request, this.methodParameter));
		InOrder ordered = inOrder(f1, f2, f3);
		ordered.verify(f1).isFiltered(this.request, this.methodParameter);
		ordered.verify(f2).isFiltered(this.request, this.methodParameter);
		ordered.verify(f3, never()).isFiltered(this.request, this.methodParameter);
	}
}

package org.springframework.springfaces.mvc.navigation.annotation.support;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockMethodParameter;

import org.junit.Test;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Tests for {@link NavigationMethodReturnValueHandler}.
 * 
 * @author Phillip Webb
 */
public class NavigationMethodReturnValueHandlerTest {

	private NavigationMethodReturnValueHandler handler = new NavigationMethodReturnValueHandler();

	@Test
	public void shouldSupportAllTypes() throws Exception {
		assertTrue(handler.supportsReturnType(mockMethodParameter(Object.class)));
		assertTrue(handler.supportsReturnType(mockMethodParameter(String.class)));
	}

	@Test
	public void shouldSetReturnValueToView() throws Exception {
		Object returnValue = new Object();
		ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
		handler.handleReturnValue(returnValue, mockMethodParameter(Object.class), mavContainer,
				mock(NativeWebRequest.class));
		verify(mavContainer).setView(returnValue);
		verifyNoMoreInteractions(mavContainer);
	}
}

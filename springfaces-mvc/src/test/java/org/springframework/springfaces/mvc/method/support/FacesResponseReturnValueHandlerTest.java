package org.springframework.springfaces.mvc.method.support;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Tests for {@link FacesResponseReturnValueHandler}.
 * 
 * @author Phillip Webb
 */
public class FacesResponseReturnValueHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private FacesResponseReturnValueHandler handler;

	@Mock
	private FacesContext facesContext;

	@Mock
	private MethodParameter returnType;

	@Mock
	private HandlerMethodReturnValueHandler delegate;

	@Mock
	private Object returnValue;

	@Mock
	private ModelAndViewContainer mavContainer;

	@Mock
	private NativeWebRequest webRequest;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		FacesContextSetter.setCurrentInstance(facesContext);
		handler = new FacesResponseReturnValueHandler(delegate);
		given(handler.supportsReturnType(any(MethodParameter.class))).willReturn(true);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldNeedDelegateHandler() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Handler must not be null");
		new FacesResponseReturnValueHandler(null);
	}

	@Test
	public void shouldDelegateSupports() throws Exception {
		handler.supportsReturnType(returnType);
		verify(delegate).supportsReturnType(returnType);
		verifyZeroInteractions(facesContext);
	}

	@Test
	public void shouldDelegateHandle() throws Exception {
		handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
		verify(delegate).handleReturnValue(returnValue, returnType, mavContainer, webRequest);
	}

	@Test
	public void shouldWorkWithoutFacesContext() throws Exception {
		FacesContextSetter.setCurrentInstance(null);
		handler.supportsReturnType(returnType);
		handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
		verify(delegate).supportsReturnType(returnType);
		verify(delegate).handleReturnValue(returnValue, returnType, mavContainer, webRequest);
	}

	@Test
	public void shouldMarkFacesContextAsResponseComplete() throws Exception {
		handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
		verify(facesContext).responseComplete();
	}
}

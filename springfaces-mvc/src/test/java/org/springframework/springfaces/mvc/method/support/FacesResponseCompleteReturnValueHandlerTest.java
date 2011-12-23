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
 * Tests for {@link FacesResponseCompleteReturnValueHandler}.
 * 
 * @author Phillip Webb
 */
public class FacesResponseCompleteReturnValueHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private FacesResponseCompleteReturnValueHandler handler;

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
		FacesContextSetter.setCurrentInstance(this.facesContext);
		this.handler = new FacesResponseCompleteReturnValueHandler(this.delegate);
		given(this.handler.supportsReturnType(any(MethodParameter.class))).willReturn(true);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldNeedDelegateHandler() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Handler must not be null");
		new FacesResponseCompleteReturnValueHandler(null);
	}

	@Test
	public void shouldDelegateSupports() throws Exception {
		this.handler.supportsReturnType(this.returnType);
		verify(this.delegate).supportsReturnType(this.returnType);
		verifyZeroInteractions(this.facesContext);
	}

	@Test
	public void shouldDelegateHandle() throws Exception {
		this.handler.handleReturnValue(this.returnValue, this.returnType, this.mavContainer, this.webRequest);
		verify(this.delegate).handleReturnValue(this.returnValue, this.returnType, this.mavContainer, this.webRequest);
	}

	@Test
	public void shouldWorkWithoutFacesContext() throws Exception {
		FacesContextSetter.setCurrentInstance(null);
		this.handler.supportsReturnType(this.returnType);
		this.handler.handleReturnValue(this.returnValue, this.returnType, this.mavContainer, this.webRequest);
		verify(this.delegate).supportsReturnType(this.returnType);
		verify(this.delegate).handleReturnValue(this.returnValue, this.returnType, this.mavContainer, this.webRequest);
	}

	@Test
	public void shouldMarkFacesContextAsResponseComplete() throws Exception {
		this.handler.handleReturnValue(this.returnValue, this.returnType, this.mavContainer, this.webRequest);
		verify(this.facesContext).responseComplete();
	}
}

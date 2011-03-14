package org.springframework.springfaces.internal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.withSettings;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.web.context.WebApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class WrapperHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private FacesContext facesContext;

	@Mock
	private ExternalContext externalContext;

	private Map<String, Object> requestMap = new HashMap<String, Object>();

	private Map<String, Object> applicationMap = new HashMap<String, Object>();

	@Mock
	private WebApplicationContext applicationContext;

	@SuppressWarnings("rawtypes")
	private Map<String, FacesWrapperFactory> facesWrapperFactoryBeans = new LinkedHashMap<String, FacesWrapperFactory>();

	@Mock
	private FacesWrapperFactory<Object> factory;

	private Object delegate;

	private WrapperHandler<Object> wrapperHandler;

	@Before
	public void setup() {
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequestMap()).willReturn(requestMap);
		given(externalContext.getApplicationMap()).willReturn(applicationMap);
		given(applicationContext.getBeansOfType(FacesWrapperFactory.class)).willReturn(facesWrapperFactoryBeans);
		this.delegate = new Object();
		this.wrapperHandler = WrapperHandler.get(Object.class, delegate);
	}

	@After
	public void cleanupFacesContext() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			fc.release();
		}
	}

	private void setupApplicationContext(Object dispatchServletContext, Object webContext) {
		requestMap.put(WrapperHandler.DISPATCHER_SERVLET_WEB_APPLICATION_CONTEXT_ATTRIBUTE, dispatchServletContext);
		applicationMap.put(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webContext);
	}

	private Object setupWrapperFactory() {
		FacesContextSetter.setCurrentInstance(facesContext);
		setupApplicationContext(applicationContext, null);
		facesWrapperFactoryBeans.put("bean", factory);
		Object wrapped = new Object();
		given(factory.newWrapper(Object.class, delegate)).willReturn(wrapped);
		return wrapped;
	}

	@Test
	public void shouldNeedTypeClass() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("TypeClass must not be null");
		new WrapperHandler<Object>(null, delegate);
	}

	@Test
	public void shouldNeedDelegate() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Delegate must not be null");
		new WrapperHandler<Object>(Object.class, null);
	}

	@Test
	public void shouldReturnDelegateWithoutApplicationContext() throws Exception {
		FacesContextSetter.setCurrentInstance(facesContext);
		Object actual = wrapperHandler.getWrapped();
		assertSame(delegate, actual);
	}

	@Test
	public void shouldWrap() throws Exception {
		Object wrapped = setupWrapperFactory();
		Object actual = wrapperHandler.getWrapped();
		assertSame(wrapped, actual);
	}

	@Test
	public void shouldCacheWrapped() throws Exception {
		setupWrapperFactory();
		wrapperHandler.getWrapped();
		wrapperHandler.getWrapped();
		verify(factory).newWrapper(Object.class, delegate);
		verifyNoMoreInteractions(factory);
	}

	@Test
	public void shouldFindDispatchServiceApplicationContextFirst() throws Exception {
		FacesContextSetter.setCurrentInstance(facesContext);
		WebApplicationContext dispatchServletContext = mock(WebApplicationContext.class);
		WebApplicationContext webContext = mock(WebApplicationContext.class);
		setupApplicationContext(dispatchServletContext, webContext);
		ApplicationContext actual = wrapperHandler.getApplicationContext();
		assertSame(dispatchServletContext, actual);
	}

	@Test
	public void shouldFindWebApplicationContextSecond() throws Exception {
		FacesContextSetter.setCurrentInstance(facesContext);
		WebApplicationContext dispatchServletContext = null;
		WebApplicationContext webContext = mock(WebApplicationContext.class);
		setupApplicationContext(dispatchServletContext, webContext);
		ApplicationContext actual = wrapperHandler.getApplicationContext();
		assertSame(webContext, actual);
	}

	@Test
	public void shouldRethrowRuntimeExceptionApplicationContext() throws Exception {
		FacesContextSetter.setCurrentInstance(facesContext);
		setupApplicationContext(null, new RuntimeException());
		thrown.expect(RuntimeException.class);
		wrapperHandler.getApplicationContext();
	}

	@Test
	public void shouldRethrowErrorApplicationContext() throws Exception {
		FacesContextSetter.setCurrentInstance(facesContext);
		setupApplicationContext(null, new Error());
		thrown.expect(Error.class);
		wrapperHandler.getApplicationContext();
	}

	@Test
	public void shouldThrowIfNotWebApplicationContext() throws Exception {
		FacesContextSetter.setCurrentInstance(facesContext);
		setupApplicationContext(null, "test");
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Root context attribute is not of type WebApplicationContext");
		wrapperHandler.getApplicationContext();
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void shouldWrapInOrder() throws Exception {
		FacesContextSetter.setCurrentInstance(facesContext);
		setupApplicationContext(applicationContext, null);

		FacesWrapperFactory f1 = mock(FacesWrapperFactory.class, withSettings().extraInterfaces(Ordered.class));
		FacesWrapperFactory f2 = mock(FacesWrapperFactory.class, withSettings().extraInterfaces(Ordered.class));
		FacesWrapperFactory f3 = mock(FacesWrapperFactory.class, withSettings().extraInterfaces(Ordered.class));
		given(((Ordered) f1).getOrder()).willReturn(1);
		given(((Ordered) f2).getOrder()).willReturn(2);
		given(((Ordered) f3).getOrder()).willReturn(3);

		// Insert in reverse order
		facesWrapperFactoryBeans.put("f3", f3);
		facesWrapperFactoryBeans.put("f2", f2);
		facesWrapperFactoryBeans.put("f1", f1);

		wrapperHandler.getWrapped();

		InOrder inOrder = Mockito.inOrder(f1, f2, f3);
		inOrder.verify(f1).newWrapper(any(Class.class), any());
		inOrder.verify(f2).newWrapper(any(Class.class), any());
		inOrder.verify(f3).newWrapper(any(Class.class), any());
	}

	@Test
	public void shouldFilterByGenerics() throws Exception {

		FacesContextSetter.setCurrentInstance(facesContext);
		setupApplicationContext(applicationContext, null);
		facesWrapperFactoryBeans.put("long", new LongFacesWrapperFactory());
		facesWrapperFactoryBeans.put("integer", new IntegerFacesWrapperFactory());

		WrapperHandler<Integer> wrapperHandler = WrapperHandler.get(Integer.class, 0);
		Integer actual = wrapperHandler.getWrapped();

		assertEquals(new Integer(1), actual);
	}

	@Test
	public void shouldPostProcessWrapper() throws Exception {
		Object wrapped = setupWrapperFactory();
		WrapperHandler<Object> spy = spy(wrapperHandler);
		spy.getWrapped();
		verify(spy).postProcessWrapper(wrapped);
	}

	private abstract static class FacesContextSetter extends FacesContext {
		public static void setCurrentInstance(FacesContext facesContext) {
			FacesContext.setCurrentInstance(facesContext);
		}
	}

	private static class LongFacesWrapperFactory implements FacesWrapperFactory<Long> {
		public Long newWrapper(Class<?> typeClass, Long delegate) {
			return delegate + 1;
		}
	}

	private static class IntegerFacesWrapperFactory implements FacesWrapperFactory<Integer> {
		public Integer newWrapper(Class<?> typeClass, Integer delegate) {
			return delegate + 1;
		}
	}

}
